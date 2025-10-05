package org.codeberg.zenxarch.mob_variants_api.spawn_conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.util.math.floatprovider.FloatProvider;

public record RandomSpawnCondition(FloatProvider provider) implements SpawnCondition {

  public static final MapCodec<RandomSpawnCondition> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      FloatProvider.createValidatedCodec(0f, 1f)
                          .fieldOf("provider")
                          .forGetter(RandomSpawnCondition::provider))
                  .apply(instance, RandomSpawnCondition::new));

  @Override
  public boolean test(SpawnContext ctx) {
    var random = ctx.world().getRandom();
    return random.nextFloat() < provider.get(random);
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
