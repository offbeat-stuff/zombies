package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import org.codeberg.zenxarch.zombies.random.LerpUtils;

public class TimeConfig extends ReflectiveConfig {

  public final PhaseLength EASY = new PhaseLength(2, 500);
  public final PhaseLength NORMAL = new PhaseLength(1, 250);
  public final PhaseLength HARD = new PhaseLength(0, 100);

  public static class PhaseLength extends Section {
    @Comment("Length of time for which no zombies should spawn")
    public final TrackedValue<Integer> grace;

    @Comment("Length of phase where difficulty goes from 0 to 1")
    public final TrackedValue<Integer> length;

    public PhaseLength(int grace, int length) {
      this.grace = this.value(grace);
      this.length = this.value(length);
    }

    public double getDifficulty(DoubleDoublePair time) {
      var min = this.grace.value();
      var max = this.grace.value() + this.length.value();
      var dayFactor = LerpUtils.clampedLerpProgress(time.leftDouble(), min, max);
      var timeFactor = LerpUtils.clampedLerpProgress(time.rightDouble(), min, max);
      return (dayFactor + timeFactor) / 2;
    }
  }
}
