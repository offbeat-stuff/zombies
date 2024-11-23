package org.codeberg.zenxarch.zombies.datagen

import net.minecraft.enchantment.provider.ByCostWithDifficultyEnchantmentProvider
import net.minecraft.enchantment.provider.EnchantmentProvider
import net.minecraft.registry.Registerable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.EnchantmentTags
import org.codeberg.zenxarch.zombies.Zombies

interface ZEnchantmentProviders {
    companion object {
        fun bootstrap(registry: Registerable<EnchantmentProvider>) {
            val registryEntryLookup =
                registry.getRegistryLookup(RegistryKeys.ENCHANTMENT)
            registry.register(
                ZOMBIE_SPAWN_EQUIPMENT,
                ByCostWithDifficultyEnchantmentProvider(
                    registryEntryLookup.getOrThrow(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT), 5, 30
                )
            )
        }

        fun of(path: String) = RegistryKey.of(RegistryKeys.ENCHANTMENT_PROVIDER, Zombies.id(path))

        val ZOMBIE_SPAWN_EQUIPMENT: RegistryKey<EnchantmentProvider> = of("zombie_spawn_equipment")
    }
}
