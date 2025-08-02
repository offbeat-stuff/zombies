package org.codeberg.zenxarch.zombies.entity;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.data.entity.MobAttachments;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.variant.MobVariant;
import org.codeberg.zenxarch.zombies.spawning.ZombieNbtUtils;
import org.jetbrains.annotations.Nullable;

public class ExtendedZombieEntity extends ZombieEntity {
  // implements SmartBrainOwner<ExtendedZombieEntity> {

  private RegistryEntry<MobVariant> variant;

  public ExtendedZombieEntity(World world) {
    super(world);
  }

  // @Override
  // protected Profile<?> createBrainProfile() {
  //   return new SmartBrainProvider<>(this);
  // }

  // private static boolean shouldTargetEntity(LivingEntity living, ExtendedZombieEntity zombie) {
  //   if (!zombie.canTarget(living)) return false;
  //   return switch (living) {
  //     case TurtleEntity turtle -> turtle.isBaby() && !turtle.isTouchingWater();
  //     default ->
  //         living instanceof PlayerEntity
  //             || living instanceof MerchantEntity
  //             || living instanceof IronGolemEntity;
  //   };
  // }

  // @Override
  // public List<ExtendedSensor<ExtendedZombieEntity>> getSensors() {
  //   return ObjectArrayList.of(
  //       new NearbyPlayersSensor<>(),
  //       new NearbyLivingEntitySensor<ExtendedZombieEntity>()
  //           .setPredicate(ExtendedZombieEntity::shouldTargetEntity),
  //       new HurtBySensor<ExtendedZombieEntity>()
  //           .setPredicate((source, living) -> !(living instanceof ExtendedZombieEntity)),
  //       new GenericAttackTargetSensor<>(),
  //       new UnreachableTargetSensor<>());
  // }

  // @Override
  // public BrainActivityGroup<? extends ExtendedZombieEntity> getCoreTasks() {
  //   return BrainActivityGroup.coreTasks(
  //       new
  // AvoidSun<ExtendedZombieEntity>().startCondition(ExtendedZombieEntity::burnsInDaylight),
  //       new EscapeSun<ExtendedZombieEntity>()
  //           .speedModifier(1.5F)
  //           .startCondition(ExtendedZombieEntity::burnsInDaylight)
  //           .cooldownFor(zombie -> 20),
  //       new InteractWithDoor<>(),
  //       new LookAtAttackTarget<>().runFor(zombie -> zombie.getRandom().nextBetween(40, 300)),
  //       new WalkOrRunToWalkTarget<>());
  // }

  // @Override
  // public BrainActivityGroup<ExtendedZombieEntity> getIdleTasks() {
  //   return BrainActivityGroup.idleTasks(
  //       new FirstApplicableBehaviour<ExtendedZombieEntity>(
  //           new TargetOrRetaliate<>()
  //               .alertAlliesWhen((a, b) -> b instanceof PlayerEntity)
  //               .cooldownFor(z -> 20),
  //           new SetPlayerLookTarget<>(),
  //           new SetRandomLookTarget<>()),
  //       new OneRandomBehaviour<ExtendedZombieEntity>(
  //           new SetRandomWalkTarget<>(),
  //           new Idle<>().runFor(zombie -> zombie.getRandom().nextBetween(30, 60))));
  // }

  // @Override
  // public BrainActivityGroup<? extends ExtendedZombieEntity> getFightTasks() {
  //   return BrainActivityGroup.fightTasks(
  //       new InvalidateAttackTarget<>(),
  //       new TargetOrRetaliate<>()
  //           .alertAlliesWhen((a, b) -> b instanceof PlayerEntity)
  //           .cooldownFor(z -> 20),
  //       new SetWalkTargetToAttackTarget<>().speedMod(1.25F),
  //       new AnimatableMeleeAttack<>(0)
  //           .whenStarting(zombie -> zombie.setAttacking(true))
  //           .whenStopping(zombie -> zombie.setAttacking(false)),
  //       new LeapAtUnreachableTargetBehaviour<>(0)
  //           .whenStarting(zombie -> zombie.setAttacking(true))
  //           .whenStopping(zombie -> zombie.setAttacking(false)),
  //       new ReactToUnreachableTarget<>().reaction(RideMobsBehaviourImpl::rideFlyingMobs));
  // }

  // @Override
  // protected void initGoals() {
  //   /* use smartbrainlib for ai */
  // }

  // @Override
  // public void setCanBreakDoors(boolean canBreakDoors) {
  //   /* no griefing */
  // }

  // @Override
  // protected EntityNavigation createNavigation(World world) {
  //   var navigation = new SmoothGroundNavigation(this, world);
  //   navigation.setCanOpenDoors(true);
  //   return navigation;
  // }

  // @Override
  // protected void mobTick(ServerWorld world) {
  //   super.mobTick(world);
  //   tickBrain(this);
  // }

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

  private void executeEvent(
      AttachmentType<MobEffect> attachment, ServerWorld world, @Nullable LivingEntity adversary) {
    if (!this.hasAttached(attachment)) return;
    this.getAttached(attachment).run(world, this, adversary);
  }

  @Override
  public EntityData initialize(
      ServerWorldAccess world,
      LocalDifficulty difficulty,
      SpawnReason spawnReason,
      EntityData entityData) {
    if (entityData instanceof ExtendedZombieData extendedZombieData) {
      this.setVariant(extendedZombieData.getVariant());
    } else {
      ZombieVariants.getRandomVariantFromPos(world.toServerWorld(), this.getBlockPos())
          .ifPresent(this::setVariant);
    }
    var result = super.initialize(world, difficulty, spawnReason, new ZombieData(false, false));
    executeEvent(MobAttachments.ON_SPAWN, world.toServerWorld(), null);
    return result;
  }

  @Override
  protected void initEquipment(Random random, LocalDifficulty unused) {
    /* equipment is initialized in updateEnchantments */
    if (this.hasAttached(MobAttachments.EQUIPMENT_TABLE)) return;
    super.initEquipment(random, unused);
  }

  @Override
  protected void updateEnchantments(
      ServerWorldAccess world, Random random, LocalDifficulty unused) {
    if (this.hasAttached(MobAttachments.EQUIPMENT_TABLE))
      MobAttachments.initEquipment(
          this,
          world.toServerWorld(),
          this.getAttached(MobAttachments.EQUIPMENT_TABLE),
          getExtentedDifficulty(world));
    else super.updateEnchantments(world, random, unused);
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    if (!super.damage(world, source, amount)) return false;
    executeEvent(
        MobAttachments.ON_DAMAGE,
        world,
        source.getAttacker() instanceof LivingEntity living ? living : null);
    return true;
  }

  @Override
  public boolean tryAttack(ServerWorld world, Entity target) {
    var result = super.tryAttack(world, target);
    if (result && target instanceof LivingEntity living) {
      executeEvent(MobAttachments.ON_ATTACK, world, living);
    }
    return result;
  }

  @Override
  protected void onKilledBy(@Nullable LivingEntity adversary) {
    if (this.getWorld() instanceof ServerWorld world)
      executeEvent(MobAttachments.ON_KILLED, world, adversary);
    super.onKilledBy(adversary);
  }

  @Override
  public boolean onKilledOther(ServerWorld world, LivingEntity other, DamageSource source) {
    var result = super.onKilledOther(world, other, source);
    executeEvent(MobAttachments.ON_KILL, world, other);
    return result;
  }

  @Override
  public void onDeath(DamageSource damageSource) {
    if (!this.isRemoved() && !this.dead && this.getWorld() instanceof ServerWorld world) {
      executeEvent(
          MobAttachments.ON_DEATH,
          world,
          damageSource.getAttacker() instanceof LivingEntity adversery ? adversery : null);
    }
    super.onDeath(damageSource);
  }

  @Override
  public void tick() {
    if (this.getWorld() instanceof ServerWorld world)
      executeEvent(MobAttachments.ON_TICK, world, null);
    super.tick();
  }

  @Override
  protected void initAttributes() {
    this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS).setBaseValue(0.0);
  }

  @Override
  protected void applyAttributeModifiers(float chanceMultiplier) {
    super.applyAttributeModifiers(chanceMultiplier);
    this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS)
        .removeModifier(Identifier.ofVanilla("leader_zombie_bonus"));
  }

  @Override
  public void readCustomData(ReadView view) {
    super.readCustomData(view);
    ZombieNbtUtils.getVariantFromView(this.getWorld(), view).ifPresent(this::setVariant);
  }

  @Override
  public void writeCustomData(WriteView view) {
    super.writeCustomData(view);
    ZombieNbtUtils.setVariantToView(this.getWorld(), view, getVariant());
  }

  @Override
  public boolean canSpawn(WorldView world) {
    return world.doesNotIntersectEntities(this)
        && world.isSpaceEmpty(this)
        && (this.canSpawnAsReinforcementInFluid() || !world.containsFluid(this.getBoundingBox()));
  }

  public void setVariant(RegistryEntry<MobVariant> variant) {
    this.variant = variant;
    if (variant != null) this.variant.value().components().forEach(this::setAttachedFromVariant);
  }

  @SuppressWarnings("unchecked")
  private void setAttachedFromVariant(AttachmentType<?> attachment, Object value) {
    if (!this.hasAttached(attachment)) this.setAttached((AttachmentType<Object>) attachment, value);
  }

  public RegistryEntry<MobVariant> getVariant() {
    return this.variant;
  }

  public static class ExtendedZombieData extends ZombieData {

    private final RegistryEntry<MobVariant> variant;

    public ExtendedZombieData(
        RegistryEntry<MobVariant> variant, boolean baby, boolean tryChickenJockey) {
      super(baby, tryChickenJockey);
      this.variant = variant;
    }

    public RegistryEntry<MobVariant> getVariant() {
      return this.variant;
    }
  }
}
