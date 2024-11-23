package org.codeberg.zenxarch.zombies.loot_table

import com.mojang.serialization.MapCodec
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import org.codeberg.zenxarch.zombies.Zombies

interface ZombieLootFunctionTypes {
    companion object {
        val ENCHANTMENT_PROVIDER: LootFunctionType<EnchantmentProviderLootFunction?> =
            register("enchantment_provider", EnchantmentProviderLootFunction.CODEC)

        private fun <T : LootFunction?> register(
            id: String, codec: MapCodec<T>
        ): LootFunctionType<T> {
            return Registry.register(
                Registries.LOOT_FUNCTION_TYPE, Zombies.id(id), LootFunctionType(codec)
            )
        }

        @JvmStatic
        fun initialize() {}
    }
}
