package org.codeberg.zenxarch.zombies.math;

import java.util.List;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public abstract class RandomUtils {
  public static boolean nextBoolean(Random random, double chance) {
    return random.nextDouble() < chance;
  }

  public static boolean nextBoolean(Random random, double atLeastOnce, int times) {
    var chance = 1.0 - Math.pow(1.0 - atLeastOnce, 1.0 / times);
    return nextBoolean(random, chance);
  }

  public static double nextGaussian(Random random, double mean, double stdDev) {
    return mean + stdDev * random.nextGaussian();
  }

  public static double logDist(Random random, double mean, double stdDev) {
    return Math.exp(random.nextGaussian() * stdDev) * mean;
  }

  public static <T> Optional<T> sample(List<T> list, DoubleSupplier rng) {
    if (list.isEmpty()) return Optional.empty();
    var value = 0.0;
    while (true) {
      value = rng.getAsDouble();
      if (value >= 0.0 && value <= 1.0) break;
    }
    var index = MathHelper.clamp((int) (value * list.size()), 0, list.size() - 1);
    return Optional.of(list.get(index));
  }
}
