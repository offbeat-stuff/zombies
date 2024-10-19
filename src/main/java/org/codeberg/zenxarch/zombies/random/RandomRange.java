package org.codeberg.zenxarch.zombies.random;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public record RandomRange(double min, double max) {

  private double chance(double progress) {
    return MathHelper.lerp(progress, this.min, this.max);
  }

  public boolean nextBoolean(Random random, double progress) {
    return RandomUtils.nextBoolean(random, chance(progress));
  }

  public boolean nextBoolean(double progress) {
    return chance(progress) >= 0.0;
  }
}
