package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.FloatRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.IntegerRange;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class SpawnConfig extends ReflectiveConfig {
  @Comment("Distance near player along which no spawns shall occur")
  @IntegerRange(min = 0, max = 128)
  public final TrackedValue<Integer> NO_SPAWN_NEAR_PLAYER_RANGE = this.value(16);

  @Comment("Range from player within which random spawn positions will be used")
  @IntegerRange(min = 0, max = 128)
  public final TrackedValue<Integer> SPAWN_RANGE_FROM_PLAYER = this.value(64);

  @Comment("Range from initial within which extra spawns will be attempted")
  @IntegerRange(min = 0, max = 128)
  public final TrackedValue<Integer> SPAWN_RANGE_FROM_INITIAL_POINT = this.value(16);

  @Comment("Biomes in which zombies spawning should be cancelled")
  public final TrackedValue<ValueList<RegistryConfigEntry>> NO_SPAWN_IN_BIOMES =
      this.value(
          ConfigUtils.tagList(
              builder ->
                  builder.add(
                      BiomeTags.WITHOUT_ZOMBIE_SIEGES, BiomeTags.ANCIENT_CITY_HAS_STRUCTURE)));

  @Comment("List of biomes with spawn chance increasing as difficulty goes up")
  @Comment("Chance goes from 0.0 to 1.0")
  public final TrackedValue<ValueList<RegistryConfigEntry>> SPECIAL_SPAWN_BIOMES =
      this.value(
          ConfigUtils.tagList(builder -> builder.add(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE)));

  public boolean skipSpawnIn(RegistryEntry<Biome> biome, Random random, double difficulty) {
    for (var v : NO_SPAWN_IN_BIOMES.value()) if (v.value().matches(biome)) return true;
    for (var v : SPECIAL_SPAWN_BIOMES.value())
      if (v.value().matches(biome)) return !RandomUtils.nextBoolean(random, difficulty);
    return false;
  }

  @Comment("In case min equals max uses min_probability")
  @Comment("Basically if block light level is min uses min prob and at max uses max prob")
  @Comment("and in between, in case max < min values in range [0,max] U [min,16] are considered")
  public final LightSpawnRange BLOCKLIGHT =
      new LightSpawnRange(LightType.BLOCK, true, 0, 1.0f, 0, 1.0f);

  @Comment("In case min equals max uses min_probability")
  @Comment("Basically if sky light level is min uses min prob and at max uses max prob")
  @Comment("and in between, in case max < min values in range [0,max] U [min,16] are considered")
  public final LightSpawnRange SKYLIGHT =
      new LightSpawnRange(LightType.SKY, true, 0, 1.0f, 15, 0.5f);

  public static class LightSpawnRange extends Section {
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
      var chance =
          MathHelper.lerp(progress, this.min_probability.value(), this.max_probability.value());
      return RandomUtils.nextBoolean(random, chance);
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
