package org.codeberg.zenxarch.zombies.spawning

import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.random.Random
import net.minecraft.world.LightType
import org.codeberg.zenxarch.zombies.ZombieGamerules
import org.codeberg.zenxarch.zombies.math.IntRange
import org.codeberg.zenxarch.zombies.spawning.SpawnUtils.burnsZombie
import org.codeberg.zenxarch.zombies.spawning.SpawnUtils.canSpawnAtPosBasic
import org.codeberg.zenxarch.zombies.spawning.SpawnUtils.getBounds
import java.util.*
import kotlin.math.max
import kotlin.math.sqrt

object SpawnProvider {
    private val random: Random = Random.create()
    private const val SPAWN_RANGE = 80
    private const val SQ_NEARBY_RANGE = 16 * 16

    private fun isTooCloseToAny(b: BlockPos, positions: List<BlockPos>)=
        positions.any { it.getSquaredDistance(b) < SQ_NEARBY_RANGE }

    private fun validate(
        world: ServerWorld, positions: List<BlockPos>, pos: Optional<BlockPos>
    )= pos.filter { isTooCloseToAny(it, positions) }.filter { canSpawnAtPosBasic(world, it!!) }

    private fun doesNotBlockMob(world: ServerWorld, pos: BlockPos)=
        world.getBlockState(pos).getCollisionShape(world, pos).isEmpty

    private fun doesNotContainFluid(world: ServerWorld, pos: BlockPos) = world.getFluidState(pos).isEmpty

    private fun adjustPos(world: ServerWorld, ipos: BlockPos, yRange: IntRange): Optional<BlockPos> {
        val pos = ipos.mutableCopy()

        val blockLight = world.getLightLevel(LightType.BLOCK, pos)
        var ourRange = IntRange.of(blockLight, 16).shiftBy(pos.y)
        ourRange = ourRange.intersection(yRange)

        if (!ourRange.isValid) return Optional.empty()

        for (y in ourRange.iterate()) {
            pos.setY(y + 1)
            if (!doesNotBlockMob(world, pos)) return Optional.of(pos.up())
            if (!doesNotContainFluid(world, pos)) return Optional.empty()
        }

        return Optional.empty()
    }

    private fun getSpawnRangeFor(x: Int, z: Int): IntRange {
        val sqDist = x * x + z * z
        val radSq = SPAWN_RANGE * SPAWN_RANGE
        if (sqDist >= radSq) return IntRange.INVALID

        val y = MathHelper.ceil(sqrt((radSq - sqDist).toDouble()))
        return IntRange.of(y)
    }

    private fun getYRange(world: ServerWorld, centerPos: BlockPos, pos: BlockPos): IntRange {
        var range = getBounds(world, pos)

        val relx = centerPos.x - pos.x
        val relz = centerPos.z - pos.z
        var otherRange = getSpawnRangeFor(relx, relz)

        if (!otherRange.isValid) return IntRange.INVALID
        otherRange = otherRange.shiftBy(centerPos.y)

        range = range.intersection(otherRange)

        return range
    }

    fun giveSpawnPositions(
        world: ServerWorld, centerPos: BlockPos, positions: List<BlockPos>, toSpawn: Int
    ): Optional<BlockPos> {
        var spawnTries =
            (toSpawn * 100) / (world.gameRules.getInt(ZombieGamerules.SPAWN_SPEED) * 20)
        spawnTries = max(spawnTries.toDouble(), 5.0).toInt()
        for (pos in BlockPos.iterateRandomly(random, spawnTries, centerPos, SPAWN_RANGE)) {
            if (burnsZombie(world, centerPos)) continue

            val range = getYRange(world, centerPos, pos)
            if (!range.isValid) continue

            var nextPos = adjustPos(world, pos, range)
            nextPos = validate(world, positions, nextPos)
            if (nextPos.isPresent) return nextPos
        }
        return Optional.empty()
    }
}
