package org.codeberg.zenxarch.zombies.entity.behaviour;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.ReactToUnreachableTarget;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;

public class RideMobsBehaviour<E extends LivingEntity> extends ReactToUnreachableTarget<E> {
  public RideMobsBehaviour() {
    this.callback = RideMobsBehaviour::handleUnreachableMob;
  }

  private static void handleUnreachableMob(LivingEntity self, boolean towering) {
    if (!towering) return;
    var nearestFlyingEntityOpt = findFlyingEntity(self);
    if (nearestFlyingEntityOpt.isEmpty()) return;
    var nearestFlyingEntity = nearestFlyingEntityOpt.get();
    if (self.squaredDistanceTo(nearestFlyingEntity) > MathHelper.square(3)) return;
    self.startRiding(nearestFlyingEntity);
  }

  private static Optional<LivingEntity> findFlyingEntity(LivingEntity self) {
    return EntityRetrievalUtil.findEntity(
        self,
        10,
        e -> {
          if (e.getType().equals(EntityType.CHICKEN)) return true;
          if (e.getType().equals(EntityType.PARROT)) return true;
          return false;
        });
  }
}
