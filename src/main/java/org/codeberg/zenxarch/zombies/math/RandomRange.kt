package org.codeberg.zenxarch.zombies.math

import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random

@JvmRecord
data class RandomRange(val min: Double, val max: Double) {
    private fun chance(progress: Double): Double {
        return MathHelper.lerp(progress, this.min, this.max)
    }

    fun nextBoolean(random: Random, progress: Double): Boolean {
        return RandomUtils.nextBoolean(random, chance(progress))
    }

    fun nextBoolean(progress: Double): Boolean {
        return chance(progress) >= 0.0
    }
}
