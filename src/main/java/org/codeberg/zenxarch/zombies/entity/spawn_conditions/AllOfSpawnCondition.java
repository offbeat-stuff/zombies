package org.codeberg.zenxarch.zombies.entity.spawn_conditions;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.util.dynamic.Codecs;

public record AllOfSpawnCondition(List<SpawnCondition> conditions) implements SpawnCondition {

  public static final MapCodec<AllOfSpawnCondition> CODEC =
      Codecs.listOrSingle(SpawnCondition.CODEC)
          .fieldOf("conditions")
          .xmap(AllOfSpawnCondition::new, AllOfSpawnCondition::conditions);

  @Override
  public boolean test(SpawnContext ctx) {
    for (var condition : conditions) if (!condition.test(ctx)) return false;
    return true;
  }

  @Override
  public MapCodec<? extends SpawnCondition> getCodec() {
    return CODEC;
  }
}
