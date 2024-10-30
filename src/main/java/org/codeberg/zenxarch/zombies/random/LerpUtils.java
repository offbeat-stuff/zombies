package org.codeberg.zenxarch.zombies.random;

import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public abstract class LerpUtils {

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

  public static int recursiveSelect(Random random, double power, double chance, int size) {
    if (size == 0) return -1;
    for (int i = 0; i < size; i++) if (random.nextDouble() * power < chance) return i;
    return 0;
  }

  public static <T> T recursiveSelect(Random random, double power, double chance, List<T> list) {
    var index = recursiveSelect(random, power, chance, list.size());
    if (index == -1) return null;
    return list.get(index);
  }
}
