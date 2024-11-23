package org.codeberg.zenxarch.zombies.entity

import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.tag.BiomeTags
import org.codeberg.zenxarch.zombies.entity.LootTableBasedTemplate.Companion.builder
import org.codeberg.zenxarch.zombies.entity.ZombieRegistry.defaultId
import org.codeberg.zenxarch.zombies.entity.ZombieRegistry.freezeRegistry
import org.codeberg.zenxarch.zombies.entity.ZombieRegistry.register
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables

object ZombieTemplates {
    private fun defaultBuilder(): LootTableBasedTemplate.Builder {
        return builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT)
    }

    private fun defaultBuilder(weight: Int): LootTableBasedTemplate.Builder {
        return builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT)
            .withWeight(weight)
    }

    val COMMON_ZOMBIE: String = defaultId
    const val SWAPPING_ZOMBIE: String = "SwappingZombie"
    const val FIRE_ZOMBIE: String = "FireZombie"
    const val FREEZE_ZOMBIE: String = "FreezeZombie"

    @JvmStatic
    fun initialize() {
        register(COMMON_ZOMBIE, defaultBuilder(512))
        register(
            SWAPPING_ZOMBIE,
            defaultBuilder()
                .withOnAttack(ZombieEffect.swapPositions())
                .withOnTick(ZombieEffect.spawnParticles(ParticleTypes.PORTAL, 0.2f))
        )
        register(
            FIRE_ZOMBIE,
            defaultBuilder(16)
                .withOnAttack(ZombieEffect.ignite(1.0f))
                .withOnTick(ZombieEffect.spawnParticles(ParticleTypes.FLAME, 0.2f))
                .spawnIn(BiomeTags.SPAWNS_WARM_VARIANT_FROGS)
        )
        register(
            FREEZE_ZOMBIE,
            defaultBuilder(16)
                .withOnAttack(ZombieEffect.freeze())
                .withOnTick(ZombieEffect.spawnParticles(ParticleTypes.SNOWFLAKE, 0.2f))
                .spawnIn(BiomeTags.SPAWNS_COLD_VARIANT_FROGS)
        )
        freezeRegistry()
    }

    fun register(id: String, builder: LootTableBasedTemplate.Builder) {
        register(id, builder.build())
    }
}
