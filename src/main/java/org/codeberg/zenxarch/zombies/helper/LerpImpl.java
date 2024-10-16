package org.codeberg.zenxarch.zombies.helper;

import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public abstract class LerpImpl {

  public static Double lerp(List<Double> list, double delta) {
    if (list.isEmpty()) return 0.0;
    if (list.size() == 1) return list.get(0);
    if (delta >= 1.0) return list.getLast();
    if (delta <= 0.0) return list.getFirst();
    var properDelta = delta * (list.size() - 1);
    var subDelta = MathHelper.fractionalPart(properDelta);
    int index = (int) properDelta;
    return MathHelper.lerp(subDelta, list.get(index), list.get(index + 1));
  }

  public static boolean lerpB(List<Boolean> list, double delta) {
    return list.get((int) (delta * (list.size() - 1)));
  }

  public static double clampedLerpProgress(double value, double min, double max) {
    if (min == max) return 0.0;
    return MathHelper.clamp(MathHelper.getLerpProgress(value, min, max), 0.0, 1.0);
  }

  /**
   * @return the index to choose from list of size {@code n}
   */
  public static int lerpWeighted(Random random, double start, double end, int n) {
    if (n == 0) return -1;
    final var sum = n * (start + end) * 0.5;
    final var inc = (end - start) / (n - 1);
    var acc = start;
    var rand = random.nextDouble() * sum;
    for (int i = 0; i < (n - 1); i++) {
      rand -= acc;
      if (rand < 0) return i;
      acc += inc;
    }
    return n - 1;
  }

  public static <T> T lerpWeighted(Random random, double start, double end, List<T> list) {
    if (list.isEmpty()) return null;
    var n = lerpWeighted(random, start, end, list.size());
    return list.get(n);
  }
}
