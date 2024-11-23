package org.codeberg.zenxarch.zombies.math

import com.google.common.collect.AbstractIterator
import net.minecraft.util.math.random.Random
import kotlin.math.max
import kotlin.math.min

@JvmRecord
data class IntRange(val min: Int, val max: Int) {
    val isValid: Boolean
        get() = min <= max

    fun union(other: IntRange): IntRange {
        return of(min(min.toDouble(), other.min.toDouble()).toInt(), max(max.toDouble(), other.max.toDouble()).toInt())
    }

    fun intersection(other: IntRange): IntRange {
        return of(max(min.toDouble(), other.min.toDouble()).toInt(), min(max.toDouble(), other.max.toDouble()).toInt())
    }

    fun next(random: Random): Int {
        if (!isValid) return min
        return random.nextBetween(min, max)
    }

    fun shiftBy(shift: Int): IntRange {
        return of(min + shift, max + shift)
    }

    fun contains(value: Int) = value in min..max

    fun iterate() = min .. max

    companion object {
        val INVALID: IntRange = of(0, -1)

        fun of(min: Int, max: Int): IntRange {
            return IntRange(min, max)
        }

        fun of(range: Int): IntRange {
            return IntRange(-range, range)
        }

        fun around(center: Int, range: Int): IntRange {
            return of(range).shiftBy(center)
        }
    }
}
