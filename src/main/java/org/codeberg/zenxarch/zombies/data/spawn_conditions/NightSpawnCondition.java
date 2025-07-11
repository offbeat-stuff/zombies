package org.codeberg.zenxarch.zombies.data.spawn_conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;

public record NightSpawnCondition() implements SpawnCondition {
  public static final NightSpawnCondition INSTANCE = new NightSpawnCondition();
  public static final MapCodec<NightSpawnCondition> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public boolean test(SpawnContext ctx) {
    return ctx.world().toServerWorld().isNight();
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
