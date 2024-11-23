package org.codeberg.zenxarch.zombies.math

import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import java.util.*
import java.util.function.DoubleSupplier
import kotlin.math.exp
import kotlin.math.pow

object RandomUtils {
    fun nextBoolean(random: Random, chance: Double): Boolean {
        return random.nextDouble() < chance
    }

    fun nextBoolean(random: Random, atLeastOnce: Double, times: Int): Boolean {
        val chance = 1.0 - (1.0 - atLeastOnce).pow(1.0 / times)
        return nextBoolean(random, chance)
    }

    fun nextGaussian(random: Random, mean: Double, stdDev: Double): Double {
        return mean + stdDev * random.nextGaussian()
    }

    fun logDist(random: Random, mean: Double, stdDev: Double): Double {
        return exp(random.nextGaussian() * stdDev) * mean
    }
}
