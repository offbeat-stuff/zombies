package org.codeberg.zenxarch.zombies.spawn_conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;

public record RainingSpawnCondition() implements SpawnCondition {

  public static final RainingSpawnCondition INSTANCE = new RainingSpawnCondition();
  public static final MapCodec<RainingSpawnCondition> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public boolean test(SpawnContext ctx) {
    var world = ctx.world().toServerWorld();
    return world.isRaining();
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
