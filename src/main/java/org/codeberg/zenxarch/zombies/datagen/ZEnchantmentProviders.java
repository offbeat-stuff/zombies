package org.codeberg.zenxarch.zombies.datagen;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.provider.ByCostWithDifficultyEnchantmentProvider;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZEnchantmentProviders {
  RegistryKey<EnchantmentProvider> ZOMBIE_SPAWN_EQUIPMENT = of("zombie_spawn_equipment");

  static void bootstrap(Registerable<EnchantmentProvider> registry) {
    RegistryEntryLookup<Enchantment> registryEntryLookup =
        registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
    registry.register(
        ZOMBIE_SPAWN_EQUIPMENT,
        new ByCostWithDifficultyEnchantmentProvider(
            registryEntryLookup.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 1, 25));
  }

  static RegistryKey<EnchantmentProvider> of(String path) {
    return RegistryKey.of(RegistryKeys.ENCHANTMENT_PROVIDER, Zombies.id(path));
  }
}
