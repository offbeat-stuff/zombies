package org.codeberg.zenxarch.zombies.info;

import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.BlockPos;

public class ZombieInfo {
  public static boolean isAffectedByDaylight(ZombieEntity zombie) {
    var world = zombie.getWorld();
    if (!world.isDay() || world.isClient) {
      return false;
    }

    BlockPos blockPos =
        BlockPos.ofFloored(zombie.getX(), zombie.getEyeY(), zombie.getZ());

    return !(zombie.isWet() || zombie.inPowderSnow || zombie.wasInPowderSnow) &&
        zombie.getWorld().isSkyVisible(blockPos);
  }
}
