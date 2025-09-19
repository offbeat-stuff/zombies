package org.codeberg.zenxarch.zombies.brain;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain.Profile;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.AvoidSun;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.ReactToUnreachableTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.EscapeSun;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.InteractWithDoor;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.WalkOrRunToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.navigation.SmoothGroundNavigation;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.GenericAttackTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.custom.UnreachableTargetSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

public class ZombieBrain<T extends ZombieEntity & SmartBrainOwner<T>> {
  public Profile<?> getBrainProfile(T self) {
    return new SmartBrainProvider<T>(self);
  }

  private boolean shouldTargetEntity(LivingEntity living, T zombie) {
    if (!zombie.canTarget(living)) return false;
    return switch (living) {
      case TurtleEntity turtle -> turtle.isBaby() && !turtle.isTouchingWater();
      default ->
          living instanceof PlayerEntity
              || living instanceof MerchantEntity
              || living instanceof IronGolemEntity;
    };
  }

  public List<ExtendedSensor<T>> getSensors() {
    return ObjectArrayList.of(
        new NearbyPlayersSensor<>(),
        new NearbyLivingEntitySensor<T>().setPredicate(this::shouldTargetEntity),
        new HurtBySensor<T>().setPredicate((source, living) -> !(living instanceof T)),
        new GenericAttackTargetSensor<>(),
        new UnreachableTargetSensor<>());
  }

  public BrainActivityGroup<T> getCoreTasks(Predicate<T> burnsInDaylight) {
    return BrainActivityGroup.coreTasks(
        new AvoidSun<T>().startCondition(burnsInDaylight),
        new EscapeSun<T>()
            .speedModifier(1.5F)
            .startCondition(burnsInDaylight)
            .cooldownFor(zombie -> 20),
        new InteractWithDoor<>(),
        new LookAtAttackTarget<>().runFor(zombie -> zombie.getRandom().nextBetween(40, 300)),
        new WalkOrRunToWalkTarget<>());
  }

  public BrainActivityGroup<T> getIdleTasks() {
    return BrainActivityGroup.idleTasks(
        new FirstApplicableBehaviour<T>(
            new TargetOrRetaliate<>()
                .alertAlliesWhen((a, b) -> b instanceof PlayerEntity)
                .cooldownFor(z -> 20),
            new SetPlayerLookTarget<>(),
            new SetRandomLookTarget<>()),
        new OneRandomBehaviour<T>(
            new SetRandomWalkTarget<>(),
            new Idle<>().runFor(zombie -> zombie.getRandom().nextBetween(30, 60))));
  }

  public BrainActivityGroup<T> getFightTasks() {
    return BrainActivityGroup.fightTasks(
        new InvalidateAttackTarget<>(),
        new TargetOrRetaliate<>()
            .alertAlliesWhen((a, b) -> b instanceof PlayerEntity)
            .cooldownFor(z -> 20),
        new SetWalkTargetToAttackTarget<>().speedMod(1.25F),
        new AnimatableMeleeAttack<>(0)
            .whenStarting(zombie -> zombie.setAttacking(true))
            .whenStopping(zombie -> zombie.setAttacking(false)),
        new LeapAtUnreachableTargetBehaviour<>(0)
            .whenStarting(zombie -> zombie.setAttacking(true))
            .whenStopping(zombie -> zombie.setAttacking(false)),
        new ReactToUnreachableTarget<>().reaction(ZombieBrain::rideFlyingMobs));
  }

  public EntityNavigation createNavigation(T self, World world) {
    var navigation = new SmoothGroundNavigation(self, world);
    navigation.setCanOpenDoors(true);
    return navigation;
  }

  public static void rideFlyingMobs(LivingEntity self, boolean towering) {
    if (!towering) return;
    var nearestFlyingEntityOpt = findFlyingEntity(self);
    if (nearestFlyingEntityOpt.isEmpty()) return;
    var nearestFlyingEntity = nearestFlyingEntityOpt.get();
    if (self.squaredDistanceTo(nearestFlyingEntity) > MathHelper.square(3)) return;
    self.startRiding(nearestFlyingEntity);
  }

  private static Optional<LivingEntity> findFlyingEntity(LivingEntity self) {
    return EntityRetrievalUtil.findEntity(self, 10, e -> isRideable(e.getType()));
  }

  private static boolean isRideable(EntityType<?> type) {
    return type.equals(EntityType.PARROT) || type.equals(EntityType.CHICKEN);
  }
}
