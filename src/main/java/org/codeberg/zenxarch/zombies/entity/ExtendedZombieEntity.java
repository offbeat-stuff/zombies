package org.codeberg.zenxarch.zombies.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.ai.brain.Brain.Profile;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.AvoidSun;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.EscapeSun;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.GenericAttackTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import org.codeberg.zenxarch.zombies.ZombieEntityAttachments;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.mixin.MobEntityAccessor;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;
import org.jetbrains.annotations.Nullable;

public class ExtendedZombieEntity extends ZombieEntity
    implements SmartBrainOwner<ExtendedZombieEntity>, VariantHolder<RegistryEntry<ZombieVariant>> {

  private RegistryEntry<ZombieVariant> variant;

  public ExtendedZombieEntity(World world, RegistryEntry<ZombieVariant> variant) {
    super(world);
    this.setVariant(variant);
  }

  @Override
  protected Profile<?> createBrainProfile() {
    return new SmartBrainProvider<>(this);
  }

  private static boolean shouldTargetEntity(LivingEntity living, ExtendedZombieEntity zombie) {
    if (!zombie.canTarget(living)) return false;
    return switch (living) {
      case TurtleEntity turtle -> turtle.isBaby() && !turtle.isTouchingWater();
      default ->
          living instanceof PlayerEntity
              || living instanceof MerchantEntity
              || living instanceof IronGolemEntity;
    };
  }

  @Override
  public List<ExtendedSensor<ExtendedZombieEntity>> getSensors() {
    return ObjectArrayList.of(
        new NearbyPlayersSensor<>(),
        new NearbyLivingEntitySensor<ExtendedZombieEntity>()
            .setPredicate(ExtendedZombieEntity::shouldTargetEntity),
        new HurtBySensor<ExtendedZombieEntity>()
            .setPredicate((source, living) -> !(living instanceof ExtendedZombieEntity)),
        new GenericAttackTargetSensor<>());
  }

  @Override
  public BrainActivityGroup<? extends ExtendedZombieEntity> getCoreTasks() {
    return BrainActivityGroup.coreTasks(
        new AvoidSun<ExtendedZombieEntity>().startCondition(ExtendedZombieEntity::burnsInDaylight),
        new EscapeSun<ExtendedZombieEntity>()
            .startCondition(ExtendedZombieEntity::burnsInDaylight)
            .cooldownFor(zombie -> 20),
        new LookAtAttackTarget<>().runFor(zombie -> zombie.getRandom().nextBetween(40, 300)),
        new MoveToWalkTarget<>());
  }

  @SuppressWarnings("unchecked")
  @Override
  public BrainActivityGroup<ExtendedZombieEntity> getIdleTasks() {
    return BrainActivityGroup.idleTasks(
        new FirstApplicableBehaviour<ExtendedZombieEntity>(
            new TargetOrRetaliate<>()
                .alertAlliesWhen((a, b) -> b instanceof PlayerEntity)
                .cooldownFor(z -> 20),
            new SetPlayerLookTarget<>(),
            new SetRandomLookTarget<>()),
        new OneRandomBehaviour<ExtendedZombieEntity>(
            new SetRandomWalkTarget<>(),
            new Idle<>().runFor(zombie -> zombie.getRandom().nextBetween(30, 60))));
  }

  @Override
  public BrainActivityGroup<? extends ExtendedZombieEntity> getFightTasks() {
    return BrainActivityGroup.fightTasks(
        new InvalidateAttackTarget<>(),
        new TargetOrRetaliate<>()
            .alertAlliesWhen((a, b) -> b instanceof PlayerEntity)
            .cooldownFor(z -> 20),
        new SetWalkTargetToAttackTarget<>(),
        new AnimatableMeleeAttack<>(0)
            .whenStarting(zombie -> zombie.setAttacking(true))
            .whenStopping(zombie -> zombie.setAttacking(false)),
        new LeapAtTarget<>(0)
            .whenStarting(zombie -> zombie.setAttacking(true))
            .whenStopping(zombie -> zombie.setAttacking(false)));
  }

  @Override
  protected void initGoals() {
    /* use smartbrainlib for ai */
  }

  @Override
  public void setCanBreakDoors(boolean canBreakDoors) {
    /* no griefing */
  }

  @Override
  protected void mobTick(ServerWorld world) {
    super.mobTick(world);
    tickBrain(this);
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
    ((MobEntityAccessor) this)
        .setLootTable(Optional.of(getVariant().value().lootTableInfo().onDrop()));
    getVariant().value().events().spawn().run(world.toServerWorld(), this, this.getTarget());
    return super.initialize(world, difficulty, spawnReason, new ZombieData(false, false));
  }

  @Override
  protected void initEquipment(Random random, LocalDifficulty unused) {
    /* equipment is initialized in updateEnchantments */
  }

  @Override
  protected void updateEnchantments(
      ServerWorldAccess world, Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty(world);

    getVariant().value().initEquipment(world.toServerWorld(), this, difficulty);
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    if (!super.damage(world, source, amount)) return false;
    getVariant()
        .value()
        .events()
        .damage()
        .run(world, this, source.getAttacker() instanceof LivingEntity living ? living : null);
    return true;
  }

  @Override
  public boolean tryAttack(ServerWorld world, Entity target) {
    var result = super.tryAttack(world, target);
    if (result && target instanceof LivingEntity living) {
      getVariant().value().events().attack().run(world, this, living);
    }
    return result;
  }

  @Override
  protected void onKilledBy(@Nullable LivingEntity adversary) {
    if (this.getWorld() instanceof ServerWorld world)
      getVariant().value().events().killed().run(world, this, adversary);
    super.onKilledBy(adversary);
  }

  @Override
  public boolean onKilledOther(ServerWorld world, LivingEntity other) {
    var result = super.onKilledOther(world, other);
    getVariant().value().events().kill().run(world, this, other);
    return result;
  }

  @Override
  public void onDeath(DamageSource damageSource) {
    if (!this.isRemoved() && !this.dead && this.getWorld() instanceof ServerWorld world) {
      getVariant()
          .value()
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
      getVariant().value().events().tick().run(world, this, null);
    super.tick();
  }

  @Override
  protected void initAttributes() {
    this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).setBaseValue(0.0);
    getVariant().value().attributes().applyDefaults(this);
    var random = this.random.nextDouble() - this.random.nextDouble();
    if (random < 0.0) random *= 0.5;
    this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE)
        .setBaseValue((getWorld().isDay() ? 18.0 : 30.0) + 8.0 * random);
    if (this.random.nextDouble() < 0.8) {
      this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
      if (getWorld().isNight())
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.46);
    }
  }

  @Override
  protected void applyAttributeModifiers(float chanceMultiplier) {
    super.applyAttributeModifiers(chanceMultiplier);
    this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS)
        .removeModifier(Identifier.ofVanilla("leader_zombie_bonus"));
    getVariant().value().attributes().applyModifiers(this);
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    var registry = getWorld().getRegistryManager().getOptional(ZombieRegistryKeys.ZOMBIE_VARIANT);
    if (registry.isEmpty()) return;
    nbt.putString(ZombieApocalypse.ZOMBIE_ID_KEY, getVariant().getIdAsString());
  }

  @Override
  public boolean canSpawn(WorldView world) {
    return world.doesNotIntersectEntities(this)
        && world.isSpaceEmpty(this)
        && (this.canSpawnAsReinforcementInFluid() || !world.containsFluid(this.getBoundingBox()));
  }

  @Override
  public void setVariant(RegistryEntry<ZombieVariant> variant) {
    this.variant = variant;
    var key = this.variant.getKey();
    if (key.isEmpty()) return;
    this.setAttached(
        ZombieEntityAttachments.ZOMBIE_VARIANT_TEXTURE_OVERRIDE,
        key.get().getValue().withPrefixedPath("textures/entity/zombie/").withSuffixedPath(".png"));
  }

  @Override
  public RegistryEntry<ZombieVariant> getVariant() {
    return this.variant;
  }
}
