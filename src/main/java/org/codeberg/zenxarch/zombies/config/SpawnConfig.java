package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import net.minecraft.registry.tag.BiomeTags;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class SpawnConfig extends ReflectiveConfig {
  @Comment("Distance near player along which no spawns shall occur")
  @IntegerRange(min = 0, max = 128)
  public final TrackedValue<Integer> NO_SPAWN_NEAR_PLAYER_RANGE = this.value(16);

  @Comment("Range from player within which random spawn positions will be used")
  @IntegerRange(min = 0, max = 128)
  public final TrackedValue<Integer> SPAWN_RANGE_FROM_INITIAL_POINT = this.value(64);

  @Comment("Biomes in which mob spawning should be cancelled")
  public final TrackedValue<ValueList<BiomeConfigEntry>> NO_SPAWN_IN_BIOMES =
      this.value(
          ValueList.create(
              new BiomeConfigEntry(), new BiomeConfigEntry(BiomeTags.WITHOUT_ZOMBIE_SIEGES)));
}
