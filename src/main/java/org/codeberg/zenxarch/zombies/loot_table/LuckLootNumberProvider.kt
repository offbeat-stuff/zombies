package org.codeberg.zenxarch.zombies.loot_table

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.loot.context.LootContext
import net.minecraft.loot.provider.number.LootNumberProvider
import net.minecraft.loot.provider.number.LootNumberProviderType
import net.minecraft.util.math.MathHelper

@JvmRecord
data class LuckLootNumberProvider(val min: Float, val max: Float) : LootNumberProvider {
    override fun getType(): LootNumberProviderType {
        return ZombieLootNumberProviderTypes.LUCK
    }

    override fun nextFloat(context: LootContext): Float {
        return MathHelper.lerp(context.luck, min, max)
    }

    companion object {
        private val INSTANCE = LuckLootNumberProvider(0.0f, 1.0f)

        @JvmField
        val CODEC: MapCodec<LuckLootNumberProvider?> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<LuckLootNumberProvider?> ->
                instance
                    .group(
                        Codec.FLOAT
                            .optionalFieldOf("min", 0.0f)
                            .forGetter { it?.min },
                        Codec.FLOAT
                            .optionalFieldOf("max", 1.0f)
                            .forGetter { it?.max }
                    )
                    .apply(
                        instance
                    ) { min: Float, max: Float -> LuckLootNumberProvider(min, max) }
            }

        @JvmStatic
        fun create(): LuckLootNumberProvider {
            return INSTANCE
        }

        fun create(min: Float, max: Float): LuckLootNumberProvider {
            return LuckLootNumberProvider(min, max)
        }
    }
}
