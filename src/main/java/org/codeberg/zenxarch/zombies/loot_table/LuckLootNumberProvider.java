package org.codeberg.zenxarch.zombies.loot_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;

public class LuckLootNumberProvider implements LootNumberProvider {
  private static final LuckLootNumberProvider INSTANCE = new LuckLootNumberProvider();
  public static final MapCodec<LuckLootNumberProvider> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public LootNumberProviderType getType() {
    return ZombieLootNumberProviderTypes.LUCK;
  }

  @Override
  public float nextFloat(LootContext context) {
    return context.getLuck();
  }

  public static LuckLootNumberProvider create() {
    return INSTANCE;
  }
}
