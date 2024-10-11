package org.codeberg.zenxarch.zombies.helper;

import net.minecraft.util.math.random.Random;

public abstract class ProbabilityImpl {
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
}
