package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedName;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

public class SpawnConfig extends ReflectiveConfig {
  @Comment("Distance near player along which no spawns shall occur")
  @SerializedName("no_spawn_near_player_range")
  @IntegerRange(min = 0, max = 128)
  public TrackedValue<Integer> NO_SPAWN_NEAR_PLAYER_RANGE = this.value(16);

  @Comment("Range from player within which random spawn positions will be used")
  @SerializedName("spawn_range_from_initial_point")
  @IntegerRange(min = 0, max = 128)
  public TrackedValue<Integer> SPAWN_RANGE_FROM_INITIAL_POINT = this.value(64);
}
