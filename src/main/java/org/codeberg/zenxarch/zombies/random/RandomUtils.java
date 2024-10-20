package org.codeberg.zenxarch.zombies.random;

import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;

public abstract class RandomUtils {
  public static double nextDoubleAround(Random random, double average, double spread) {
    var min = average - spread;
    var max = average + spread;
    return nextDoubleBetween(random, min, max);
  }

  public static double nextDoubleBetween(Random random, double min, double max) {
    return random.nextDouble() * (max - min) + min;
  }

  public static boolean nextBoolean(Random random, double chance) {
    return random.nextDouble() < chance;
  }

  public static boolean nextBoolean(double random, double chance) {
    return random < chance;
  }

  public static BlockPos randomOffset(Random random, BlockPos pos, int range) {
    var axes = random.nextInt(3) + 1;
    var axis = Axis.pickRandomAxis(random);
    for (int i = 0; i < axes; i++) {
      pos = pos.offset(axis, random.nextBetween(-range, range));
      axis = AxisCycleDirection.FORWARD.cycle(axis);
    }

    return pos;
  }
}
