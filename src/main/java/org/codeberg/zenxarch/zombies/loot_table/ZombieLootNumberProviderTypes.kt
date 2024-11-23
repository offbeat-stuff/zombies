package org.codeberg.zenxarch.zombies.loot_table

import com.mojang.serialization.MapCodec
import net.minecraft.loot.provider.number.LootNumberProvider
import net.minecraft.loot.provider.number.LootNumberProviderType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.codeberg.zenxarch.zombies.Zombies

interface ZombieLootNumberProviderTypes {
    companion object {
        val LUCK: LootNumberProviderType = register("luck", LuckLootNumberProvider.CODEC)

        private fun register(
            id: String, codec: MapCodec<out LootNumberProvider?>
        ): LootNumberProviderType {
            return Registry.register(
                Registries.LOOT_NUMBER_PROVIDER_TYPE, Zombies.id(id), LootNumberProviderType(codec)
            )
        }

        @JvmStatic
        fun initialize() {}
    }
}
