package org.codeberg.zenxarch.default_zombies.datagen.dynamic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.provider.ByCostWithDifficultyEnchantmentProvider;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;

public final class ZEnchantmentProviderGenerator {

  public static DynamicRegistryInitializer<EnchantmentProvider> INITIALIZER =
      new DynamicRegistryInitializer<>(
          RegistryKeys.ENCHANTMENT_PROVIDER, ZEnchantmentProviderGenerator::bootstrap);

  public static RegistryKey<EnchantmentProvider> ZOMBIE_SPAWN_EQUIPMENT =
      INITIALIZER.of("zombie_spawn_equipment");

  public static void bootstrap(Registerable<EnchantmentProvider> registry) {
    RegistryEntryLookup<Enchantment> registryEntryLookup =
        registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
    registry.register(
        ZOMBIE_SPAWN_EQUIPMENT,
        new ByCostWithDifficultyEnchantmentProvider(
            registryEntryLookup.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5, 30));
  }
}
