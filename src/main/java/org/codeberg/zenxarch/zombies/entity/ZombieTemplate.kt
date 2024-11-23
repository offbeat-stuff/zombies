package org.codeberg.zenxarch.zombies.entity

import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect

interface ZombieTemplate {
    fun initEquipment(
        world: ServerWorld, zombie: ZombieEntity, difficulty: ExtendedDifficulty, random: Random
    )

    fun onAttackEffect(): ZombieEffect

    fun onKillEffect(): ZombieEffect

    fun onTickEffect(): ZombieEffect

    val weight: Int

    fun canSpawnIn(biome: RegistryEntry<Biome>): Boolean
}
