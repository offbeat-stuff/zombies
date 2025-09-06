package org.codeberg.zenxarch.mob_variants_api.spawn_conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;

public record DaySpawnCondition() implements SpawnCondition {

  public static final DaySpawnCondition INSTANCE = new DaySpawnCondition();
  public static final MapCodec<DaySpawnCondition> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public boolean test(SpawnContext ctx) {
    return ctx.world().toServerWorld().isDay();
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
