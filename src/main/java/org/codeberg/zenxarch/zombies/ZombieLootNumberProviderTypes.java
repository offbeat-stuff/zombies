package org.codeberg.zenxarch.zombies;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ZombieLootNumberProviderTypes {

  public static final LootNumberProviderType LUCK = register("luck", LuckLootNumberProvider.CODEC);

  private static LootNumberProviderType register(
      String id, MapCodec<? extends LootNumberProvider> codec) {
    return Registry.register(
        Registries.LOOT_NUMBER_PROVIDER_TYPE, Zombies.id(id), new LootNumberProviderType(codec));
  }

  public static void initialize() {}

  public static class LuckLootNumberProvider implements LootNumberProvider {
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
}
