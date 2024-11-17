package org.codeberg.zenxarch.zombies.loot_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZombieLootNumberProviderTypes {

  public static final LootNumberProviderType LUCK = register("luck", LuckLootNumberProvider.CODEC);

  private static LootNumberProviderType register(
      String id, MapCodec<? extends LootNumberProvider> codec) {
    return Registry.register(
        Registries.LOOT_NUMBER_PROVIDER_TYPE, Zombies.id(id), new LootNumberProviderType(codec));
  }

  public static void initialize() {}
}
