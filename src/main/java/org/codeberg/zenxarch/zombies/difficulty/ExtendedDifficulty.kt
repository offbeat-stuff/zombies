package org.codeberg.zenxarch.zombies.difficulty

import net.minecraft.entity.EquipmentSlot
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import net.minecraft.world.Difficulty
import net.minecraft.world.LocalDifficulty
import org.codeberg.zenxarch.zombies.ZombieGamerules
import org.codeberg.zenxarch.zombies.math.RandomRange
import org.codeberg.zenxarch.zombies.math.RandomUtils

class ExtendedDifficulty : LocalDifficulty {
    private val difficulty: Double
    val maxZombies: Int

    constructor(world: ServerWorld, difficulty: Double, maxZombies: Int) : super(world.difficulty, 0, 0, 0f) {
        this.difficulty = difficulty
        this.maxZombies = maxZombies
    }

    constructor(world: ServerWorld, pos: BlockPos) : super(world.difficulty, 0, 0, 0f) {
        this.difficulty = DifficultyCalculations.calculateDifficulty(world, pos)
        this.maxZombies = MathHelper.clampedLerp(
            10.0, world.gameRules.getInt(ZombieGamerules.MAX_ZOMBIES).toDouble(), this.difficulty
        ).toInt()
    }

    override fun getLocalDifficulty(): Float {
        return (this.difficulty * 6.75).toFloat()
    }

    override fun isAtLeastHard(): Boolean {
        return localDifficulty >= Difficulty.HARD.ordinal.toFloat()
    }

    override fun isHarderThan(difficulty: Float): Boolean {
        return localDifficulty > difficulty
    }

    override fun getClampedLocalDifficulty(): Float {
        return difficulty.toFloat()
    }

    fun shouldEnchantEquipment(): Boolean {
        return shouldEnchant.nextBoolean(random, difficulty)
    }

    fun shouldSpawnWithEquipment(slot: EquipmentSlot): Boolean {
        return when (slot) {
            EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND -> RandomUtils.nextBoolean(
                random,
                this.difficulty
            )

            else -> RandomUtils.nextBoolean(
                random,
                this.difficulty, 4
            )
        }
    }

    companion object {
        private val random: Random = Random.create()

        private val shouldEnchant = RandomRange(-0.5, 0.5)
    }
}
