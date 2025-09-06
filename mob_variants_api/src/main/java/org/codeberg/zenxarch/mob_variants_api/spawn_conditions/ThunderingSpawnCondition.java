package org.codeberg.zenxarch.mob_variants_api.spawn_conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;

public record ThunderingSpawnCondition() implements SpawnCondition {

  public static final ThunderingSpawnCondition INSTANCE = new ThunderingSpawnCondition();
  public static final MapCodec<ThunderingSpawnCondition> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public boolean test(SpawnContext ctx) {
    var world = ctx.world().toServerWorld();
    return world.isThundering();
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
