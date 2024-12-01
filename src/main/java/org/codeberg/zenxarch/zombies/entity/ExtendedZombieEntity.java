package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
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
    return true;
  }

  @Override
  public boolean tryAttack(ServerWorld world, Entity target) {
    var result = super.tryAttack(world, target);
    if (result && target instanceof LivingEntity living) {
      this.template.onAttackEffect().run(world, this, living);
    }
    return result;
  }

  @Override
  protected void onKilledBy(@Nullable LivingEntity adversary) {
    if (this.getWorld() instanceof ServerWorld world)
      this.template.onKillEffect().run(world, this, adversary);
    super.onKilledBy(adversary);
  }

  @Override
  public void tick() {
    if (this.getWorld() instanceof ServerWorld world)
      this.template.onTickEffect().run(world, this, null);
    super.tick();
  }

  @Override
  protected void applyAttributeModifiers(float chanceMultiplier) {
    super.applyAttributeModifiers(chanceMultiplier);
    var spawnAttribute = this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS);
    spawnAttribute.setBaseValue(0.0);
    spawnAttribute.removeModifier(Identifier.ofVanilla("leader_zombie_bonus"));
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
