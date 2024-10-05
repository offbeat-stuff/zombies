package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.FloatRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;

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

  public final LightSpawnRange BLOCKLIGHT =
      new LightSpawnRange(LightType.BLOCK, true, 0, 1.0f, 0, 1.0f);
  public final LightSpawnRange SKYLIGHT =
      new LightSpawnRange(LightType.SKY, true, 0, 1.0f, 15, 0.1f);

  public static class LightSpawnRange extends Section {
    @Comment("In case min == max uses min_probability")
    public final TrackedValue<Boolean> enabled;

    @IntegerRange(min = 0, max = 15)
    public final TrackedValue<Integer> min;

    @FloatRange(min = 0.0, max = 1.0)
    public final TrackedValue<Float> min_probability;

    @IntegerRange(min = 0, max = 15)
    public final TrackedValue<Integer> max;

    @FloatRange(min = 0.0, max = 1.0)
    public final TrackedValue<Float> max_probability;

    private final transient LightType type;

    public LightSpawnRange(
        LightType type,
        boolean enabled,
        int min,
        float min_probability,
        int max,
        float max_probability) {
      this.type = type;
      this.enabled = this.value(enabled);
      this.min = this.value(min);
      this.min_probability = this.value(min_probability);
      this.max = this.value(max);
      this.max_probability = this.value(max_probability);
    }

    public boolean test(ServerWorld world, BlockPos pos, Random random) {
      return this.test(world.getLightLevel(this.type, pos), random);
    }

    public boolean test(int value, Random random) {
      if (!this.enabled.value()) return true;
      var progress = getProgress(value, this.min.value(), this.max.value());
      if (progress < 0.0 || progress > 1.0) return false;
      return random.nextDouble()
          < MathHelper.lerp(progress, this.min_probability.value(), this.max_probability.value());
    }

    private static double getProgress(int value, int min, int max) {
      if (min == max) return value == min ? 0.0 : -1.0;
      if (max < min) {
        max += 16;
        if (value < min) value += 16;
      }
      return MathHelper.getLerpProgress(value, min, max);
    }
  }
}
