package org.codeberg.zenxarch.zombies.spawn_conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.world.biome.Biome.Precipitation;

public record PrecipitationSpawnCondition(Precipitation precipitation) implements SpawnCondition {

  public static final MapCodec<PrecipitationSpawnCondition> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Precipitation.CODEC
                          .fieldOf("precipitation")
                          .forGetter(PrecipitationSpawnCondition::precipitation))
                  .apply(instance, PrecipitationSpawnCondition::new));

  @Override
  public boolean test(SpawnContext ctx) {
    var world = ctx.world().toServerWorld();
    return world.getPrecipitation(ctx.pos()).equals(this.precipitation);
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
