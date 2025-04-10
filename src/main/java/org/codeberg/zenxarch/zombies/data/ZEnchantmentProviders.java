package org.codeberg.zenxarch.zombies.data;

import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZEnchantmentProviders {
  RegistryKey<EnchantmentProvider> ZOMBIE_SPAWN_EQUIPMENT = of("zombie_spawn_equipment");

  static RegistryKey<EnchantmentProvider> of(String path) {
    return RegistryKey.of(RegistryKeys.ENCHANTMENT_PROVIDER, Zombies.id(path));
  }
}
