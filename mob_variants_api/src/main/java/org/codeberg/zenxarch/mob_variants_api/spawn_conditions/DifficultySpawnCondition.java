package org.codeberg.zenxarch.mob_variants_api.spawn_conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.util.math.floatprovider.FloatProvider;

public record DifficultySpawnCondition(FloatProvider provider) implements SpawnCondition {
  public static final MapCodec<DifficultySpawnCondition> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      FloatProvider.createValidatedCodec(0f, 1f)
                          .fieldOf("provider")
                          .forGetter(DifficultySpawnCondition::provider))
                  .apply(instance, DifficultySpawnCondition::new));

  @Override
  public boolean test(SpawnContext ctx) {
    var difficulty = ctx.world().getLocalDifficulty(ctx.pos()).getClampedLocalDifficulty();
    return difficulty < provider.get(ctx.world().getRandom());
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
