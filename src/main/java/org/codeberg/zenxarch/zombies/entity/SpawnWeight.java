package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public record SpawnWeight(int weight, int quality) {
  public static final Codec<SpawnWeight> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codec.INT.optionalFieldOf("weight", 1).forGetter(SpawnWeight::weight),
                      Codec.INT.optionalFieldOf("quality", 0).forGetter(SpawnWeight::quality))
                  .apply(instance, SpawnWeight::new));

  public static final SpawnWeight DEFAULT = new SpawnWeight(1, 0);

  public int get(ExtendedDifficulty difficulty) {
    return weight + (int) (quality * difficulty.getClampedLocalDifficulty());
  }
}
