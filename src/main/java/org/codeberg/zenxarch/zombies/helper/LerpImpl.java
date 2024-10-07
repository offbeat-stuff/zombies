package org.codeberg.zenxarch.zombies.helper;

import java.util.List;
import net.minecraft.util.math.MathHelper;

public abstract class LerpImpl {

  public static Double lerp(List<Double> list, double delta) {
    if (delta >= 1.0) return list.getLast();
    if (delta <= 0.0) return list.getFirst();
    double properDelta = delta * (list.size() - 1);
    float subDelta = (float) MathHelper.fractionalPart(properDelta);
    int index = (int) properDelta;
    return MathHelper.lerp(subDelta, list.get(index), list.get(index + 1));
  }

  public static boolean lerpB(List<Boolean> list, double delta) {
    return list.get((int) (delta * (list.size() - 1)));
  }

  public static double clampedLerpProgress(double value, double min, double max) {
    if (min == max || value < min) return 0.0;
    if (value > max) return 1.0;

    return MathHelper.getLerpProgress(value, min, max);
  }
}
