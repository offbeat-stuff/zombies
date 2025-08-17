package org.codeberg.zenxarch.zombies.spawn_conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;

public record NegateSpawnCondition(SpawnCondition condition) implements SpawnCondition {
  public static final MapCodec<NegateSpawnCondition> CODEC =
      SpawnCondition.CODEC
          .fieldOf("condition")
          .xmap(NegateSpawnCondition::new, NegateSpawnCondition::condition);

  @Override
  public boolean test(SpawnContext ctx) {
    return !condition.test(ctx);
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
