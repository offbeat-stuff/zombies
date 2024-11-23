package org.codeberg.zenxarch.zombies.loot_table

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.provider.EnchantmentProvider
import net.minecraft.item.ItemStack
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.function.LootFunction
import net.minecraft.loot.function.LootFunctionType
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty

@JvmRecord
data class EnchantmentProviderLootFunction(val provider: RegistryKey<EnchantmentProvider>) :
    LootFunction {
    override fun apply(stack: ItemStack, context: LootContext): ItemStack {
        EnchantmentHelper.applyEnchantmentProvider(
            stack,
            context.world.registryManager,
            provider,
            ExtendedDifficulty(context.world, context.luck.toDouble(), 0),
            context.random
        )
        return stack
    }

    override fun getType(): LootFunctionType<out LootFunction?> = ZombieLootFunctionTypes.ENCHANTMENT_PROVIDER

    companion object {
        @JvmField
        val CODEC: MapCodec<EnchantmentProviderLootFunction?> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<EnchantmentProviderLootFunction?> ->
                instance.group(
                    RegistryKey.createCodec(RegistryKeys.ENCHANTMENT_PROVIDER).fieldOf("provider")
                        .forGetter { it?.provider }).apply(
                    instance, ::EnchantmentProviderLootFunction
                )
            }
    }
}
