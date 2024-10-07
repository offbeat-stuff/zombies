package org.codeberg.zenxarch.zombies.config;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public abstract class RegistryEntries {
  public static RegistryConfigEntry<Biome> biomeEntry() {
    return new RegistryConfigEntry<Biome>(RegistryKeys.BIOME);
  }

  public static RegistryConfigEntry<Biome> biomeEntry(TagKey<Biome> tag) {
    return biomeEntry().withTag(tag);
  }

  public static RegistryConfigEntry<Biome> biomeEntry(RegistryKey<Biome> key) {
    return biomeEntry().withKey(key);
  }

  public static RegistryConfigEntry<Enchantment> enchantmentEntry() {
    return new RegistryConfigEntry<Enchantment>(RegistryKeys.ENCHANTMENT);
  }

  public static RegistryConfigEntry<Enchantment> enchantmentEntry(TagKey<Enchantment> tag) {
    return enchantmentEntry().withTag(tag);
  }

  public static RegistryConfigEntry<Enchantment> enchantmentEntry(RegistryKey<Enchantment> key) {
    return enchantmentEntry().withKey(key);
  }
}
