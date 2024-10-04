package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;

public class SpawnConfig extends WrappedConfig {
  @Comment("Distance near player along which no spawns shall occur")
  @IntegerRange(min = 0, max = 128)
  public int NO_SPAWN_NEAR_PLAYER_DISTANCE = 16;

  @Comment("Range from player within which random spawn positions will be used")
  @IntegerRange(min = 0, max = 128)
  public int SPAWN_RANGE_FROM_INITIAL_POINT = 64;
}
