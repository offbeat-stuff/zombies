package org.codeberg.zenxarch.zombies.entity

import net.minecraft.entity.EquipmentTable
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.loot.LootTable
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.context.LootWorldContext
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty
import org.codeberg.zenxarch.zombies.entity.effect.AllOfZombieEffect
import org.codeberg.zenxarch.zombies.entity.effect.DefaultZombieEffect
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect
import java.util.function.Predicate

@JvmRecord
data class LootTableBasedTemplate(
    val equipmentTable: EquipmentTable,
    val onAttack: ZombieEffect,
    val onDeath: ZombieEffect,
    val onTick: ZombieEffect,
    val biomePredicate: Predicate<RegistryEntry<Biome>>,
    override val weight: Int
) : ZombieTemplate {
    override fun initEquipment(
        world: ServerWorld, zombie: ZombieEntity, difficulty: ExtendedDifficulty, random: Random
    ) {
        zombie.setEquipmentFromTable(
            equipmentTable.lootTable(),
            LootWorldContext.Builder(world)
                .add(LootContextParameters.ORIGIN, zombie.pos)
                .add(LootContextParameters.THIS_ENTITY, zombie)
                .luck(difficulty.clampedLocalDifficulty)
                .build(LootContextTypes.EQUIPMENT),
            equipmentTable.slotDropChances()
        )
    }

    override fun onAttackEffect(): ZombieEffect {
        return onAttack
    }

    override fun onKillEffect(): ZombieEffect {
        return onDeath
    }

    override fun onTickEffect(): ZombieEffect {
        return onTick
    }

    override fun canSpawnIn(biome: RegistryEntry<Biome>): Boolean {
        return biomePredicate.test(biome)
    }

    class Builder(private val table: EquipmentTable) {
        private var onAttack: ZombieEffect
        private var onDeath: ZombieEffect
        private var onTick: ZombieEffect
        private var weight = 1
        private var biomePredicate: Predicate<RegistryEntry<Biome>> = DEFAULT_BIOME_PREDICATE

        init {
            this.onAttack = DefaultZombieEffect.create()
            this.onDeath = DefaultZombieEffect.create()
            this.onTick = DefaultZombieEffect.create()
        }

        fun withOnAttack(vararg effects: ZombieEffect?): Builder {
            this.onAttack = AllOfZombieEffect.create(*effects)
            return this
        }

        fun withOnDeath(vararg effects: ZombieEffect?): Builder {
            this.onDeath = AllOfZombieEffect.create(*effects)
            return this
        }

        fun withOnTick(vararg effects: ZombieEffect?): Builder {
            this.onTick = AllOfZombieEffect.create(*effects)
            return this
        }

        fun withWeight(weight: Int): Builder {
            this.weight = weight
            return this
        }

        fun spawnIn(tag: TagKey<Biome>?): Builder {
            return andBiomePredicate { b: RegistryEntry<Biome> -> b.isIn(tag) }
        }

        fun cannotSpawnIn(tag: TagKey<Biome>?): Builder {
            return andBiomePredicate { b: RegistryEntry<Biome> -> !b.isIn(tag) }
        }

        fun andBiomePredicate(predicate: Predicate<RegistryEntry<Biome>>): Builder {
            this.biomePredicate =
                if (this.biomePredicate === DEFAULT_BIOME_PREDICATE)
                    predicate
                else
                    biomePredicate.and(predicate)
            return this
        }

        fun build(): LootTableBasedTemplate {
            return LootTableBasedTemplate(table, onAttack, onDeath, onTick, biomePredicate, weight)
        }

        companion object {
            private val DEFAULT_BIOME_PREDICATE: Predicate<RegistryEntry<Biome>> =
                Predicate { true }
        }
    }

    companion object {
        @JvmStatic
        fun builder(table: EquipmentTable): Builder {
            return Builder(table)
        }

        fun builder(table: RegistryKey<LootTable?>?, slotDropChances: Float): Builder {
            return Builder(EquipmentTable(table, slotDropChances))
        }
    }
}
