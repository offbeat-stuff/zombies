package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.mixin.MobEntityAccessor;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;
import org.jetbrains.annotations.Nullable;

public class ExtendedZombieEntity extends ZombieEntity {

  private final ZombieTemplate template;

  public ExtendedZombieEntity(World world, ZombieTemplate template) {
    super(world);
    this.template = template;
  }

  @Override
  protected boolean burnsInDaylight() {
    if (this.getWorld() instanceof ServerWorld sw)
      return sw.getGameRules().getBoolean(ZombieGamerules.ZOMBIES_BURN_IN_DAYLIGHT);
    return false;
  }

  protected ExtendedDifficulty getExtentedDifficulty(ServerWorldAccess serverWorld) {
    return new ExtendedDifficulty(serverWorld.toServerWorld(), this.getBlockPos());
  }

  public void initialize(ServerWorldAccess world) {
    this.initialize(world, world.getLocalDifficulty(this.getBlockPos()), SpawnReason.NATURAL, null);
  }

  @Override
  public EntityData initialize(
      ServerWorldAccess world,
      LocalDifficulty difficulty,
      SpawnReason spawnReason,
      EntityData entityData) {
    ((MobEntityAccessor) this).setLootTable(Optional.of(this.template.lootTableInfo().onDrop()));
    this.template.events().spawn().run(world.toServerWorld(), this, this.getTarget());
    entityData = new ZombieData(false, false);
    return super.initialize(world, difficulty, spawnReason, entityData);
  }

  @Override
  protected void initEquipment(Random random, LocalDifficulty unused) {}

  @Override
  protected void updateEnchantments(
      ServerWorldAccess world, Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty(world);

    this.template.initEquipment(world.toServerWorld(), this, difficulty);
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    if (!super.damage(world, source, amount)) return false;
    if (!(world instanceof SpawnerProvider spawnerProvider)) return false;
    for (var spawner : spawnerProvider.getSpawners()) spawner.spawn(world, true);
    this.template
        .events()
        .damage()
        .run(world, this, source.getAttacker() instanceof LivingEntity living ? living : null);
    return true;
  }

  @Override
  public boolean tryAttack(ServerWorld world, Entity target) {
    var result = super.tryAttack(world, target);
    if (result && target instanceof LivingEntity living) {
      this.template.events().attack().run(world, this, living);
    }
    return result;
  }

  @Override
  protected void onKilledBy(@Nullable LivingEntity adversary) {
    if (this.getWorld() instanceof ServerWorld world)
      this.template.events().killed().run(world, this, adversary);
    super.onKilledBy(adversary);
  }

  @Override
  public boolean onKilledOther(ServerWorld world, LivingEntity other) {
    var result = super.onKilledOther(world, other);
    this.template.events().kill().run(world, this, other);
    return result;
  }

  @Override
  public void onDeath(DamageSource damageSource) {
    if (!this.isRemoved() && !this.dead && this.getWorld() instanceof ServerWorld world) {
      this.template
          .events()
          .death()
          .run(
              world,
              this,
              damageSource.getAttacker() instanceof LivingEntity adversery ? adversery : null);
    }
    super.onDeath(damageSource);
  }

  @Override
  public void tick() {
    if (this.getWorld() instanceof ServerWorld world)
      this.template.events().tick().run(world, this, null);
    super.tick();
  }

  @Override
  protected void initAttributes() {
    this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).setBaseValue(0.0);
    var random = this.random.nextDouble() - this.random.nextDouble();
    if (random < 0.0) random *= 0.5;
    this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE)
        .setBaseValue((getWorld().isDay() ? 18.0 : 30.0) + 8.0 * random);
  }

  @Override
  protected void applyAttributeModifiers(float chanceMultiplier) {
    super.applyAttributeModifiers(chanceMultiplier);
    this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS)
        .removeModifier(Identifier.ofVanilla("leader_zombie_bonus"));
    this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED)
        .addPersistentModifier(
            new EntityAttributeModifier(
                Zombies.id("zombie_night_speed_bonus"),
                random.nextTriangular(0.5, 0.25),
                Operation.ADD_MULTIPLIED_BASE));
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    var registry =
        getWorld().getRegistryManager().getOptional(ZombieRegistries.TEMPLATE_REGISTRY_KEY);
    if (registry.isEmpty()) return;
    nbt.putString(ZombieApocalypse.ZOMBIE_ID_KEY, registry.get().getId(this.template).toString());
  }

  @Override
  public boolean canSpawn(WorldView world) {
    return world.doesNotIntersectEntities(this)
        && world.isSpaceEmpty(this)
        && (this.canSpawnAsReinforcementInFluid() || !world.containsFluid(this.getBoundingBox()));
  }
}
