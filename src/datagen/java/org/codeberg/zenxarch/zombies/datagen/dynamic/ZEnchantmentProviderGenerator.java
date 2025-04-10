package org.codeberg.zenxarch.zombies.datagen.dynamic;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.provider.ByCostWithDifficultyEnchantmentProvider;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import org.codeberg.zenxarch.zombies.data.ZEnchantmentProviders;

public final class ZEnchantmentProviderGenerator
    implements DynamicRegistryInitializer<EnchantmentProvider> {

  @Override
  public void bootstrap(Registerable<EnchantmentProvider> registry) {
    RegistryEntryLookup<Enchantment> registryEntryLookup =
        registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
    registry.register(
        ZEnchantmentProviders.ZOMBIE_SPAWN_EQUIPMENT,
        new ByCostWithDifficultyEnchantmentProvider(
            registryEntryLookup.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5, 30));
  }

  @Override
  public RegistryKey<? extends Registry<EnchantmentProvider>> getRegistryKey() {
    return RegistryKeys.ENCHANTMENT_PROVIDER;
  }
}
