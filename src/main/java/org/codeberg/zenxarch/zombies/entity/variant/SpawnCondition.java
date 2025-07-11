package org.codeberg.zenxarch.zombies.entity.variant;

import com.mojang.serialization.MapCodec;

public interface SpawnCondition {
  boolean test(SpawnContext ctx);
  MapCodec<? extends SpawnCondition> getCodec();
}
