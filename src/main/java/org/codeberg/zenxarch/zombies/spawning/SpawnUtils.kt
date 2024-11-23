package org.codeberg.zenxarch.zombies.spawning

import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnRestriction
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.Heightmap
import net.minecraft.world.LightType
import org.codeberg.zenxarch.zombies.ZombieGamerules
import org.codeberg.zenxarch.zombies.data.ZBiomeTags
import org.codeberg.zenxarch.zombies.math.IntRange

object SpawnUtils {
    @JvmStatic
    fun burnsZombie(world: ServerWorld, pos: BlockPos?)=
        world.gameRules.getBoolean(ZombieGamerules.ZOMBIES_BURN_IN_DAYLIGHT)
                && world.isDay
                && world.isSkyVisible(pos)

    private fun getBounds(world: ServerWorld, x: Int, z: Int)=
        IntRange(
            world.bottomY, world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z)
        )

    @JvmStatic
    fun getBounds(world: ServerWorld, pos: BlockPos)=
        getBounds(world, pos.x, pos.z)

    private fun doesPosAllowSpawning(world: ServerWorld, pos: BlockPos): Boolean {
        if (burnsZombie(world, pos)) return false
        if (world.getLightLevel(LightType.BLOCK, pos) > 0) return false
        if (world.getBiome(pos).isIn(ZBiomeTags.WITHOUT_ZOMBIE_APOCALYPSE)) return false
        if (!SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos)) return false
        return true
    }

    private fun doesEntityCollide(world: ServerWorld, pos: Vec3d): Boolean {
        val boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX(), pos.getY(), pos.getZ())

        return !world.containsFluid(boundingBox) && world.isSpaceEmpty(boundingBox)
                && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox))
    }

    @JvmStatic
    fun canSpawnAtPosBasic(world: ServerWorld, pos: BlockPos): Boolean {
        if (!doesPosAllowSpawning(world, pos)) return false

        val spawnPos = pos.toBottomCenterPos()
        return doesEntityCollide(world, spawnPos)
    }
}
