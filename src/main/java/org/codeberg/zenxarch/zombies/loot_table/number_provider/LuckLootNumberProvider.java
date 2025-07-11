package org.codeberg.zenxarch.zombies.loot_table.number_provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.util.math.MathHelper;

public record LuckLootNumberProvider(float min, float max) implements LootNumberProvider {
  private static final LuckLootNumberProvider INSTANCE = new LuckLootNumberProvider(0.0F, 1.0F);
  public static final MapCodec<LuckLootNumberProvider> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Codec.FLOAT
                          .optionalFieldOf("min", 0.0F)
                          .forGetter(LuckLootNumberProvider::min),
                      Codec.FLOAT
                          .optionalFieldOf("max", 1.0F)
                          .forGetter(LuckLootNumberProvider::max))
                  .apply(instance, LuckLootNumberProvider::new));

  @Override
  public LootNumberProviderType getType() {
    return ZombieLootNumberProviderTypes.LUCK;
  }

  @Override
  public float nextFloat(LootContext context) {
    return MathHelper.lerp(context.getLuck(), min, max);
  }

  public static LuckLootNumberProvider create() {
    return INSTANCE;
  }

  public static LuckLootNumberProvider create(float min, float max) {
    return new LuckLootNumberProvider(min, max);
  }
}
