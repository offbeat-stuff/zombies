package org.codeberg.zenxarch.zombies.loot_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZombieLootFunctionTypes {
  public static final LootFunctionType<EnchantmentProviderLootFunction> ENCHANTMENT_PROVIDER =
      register("enchantment_provider", EnchantmentProviderLootFunction.CODEC);

  private static <T extends LootFunction> LootFunctionType<T> register(
      String id, MapCodec<T> codec) {
    return Registry.register(
        Registries.LOOT_FUNCTION_TYPE, Zombies.id(id), new LootFunctionType<>(codec));
  }

  public static void initialize() {}
}
