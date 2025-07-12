package org.codeberg.zenxarch.zombies.entity.behaviour;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

public interface RideMobsBehaviourImpl {

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
