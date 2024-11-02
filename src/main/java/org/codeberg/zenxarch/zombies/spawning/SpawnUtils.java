package org.codeberg.zenxarch.zombies.spawning;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import org.codeberg.zenxarch.zombies.random.IntRange;

public abstract class SpawnUtils {

  public static IntRange getBounds(ServerWorld world, int x, int z) {
    return IntRange.of(
        world.getBottomY(), world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z));
  }

  public static IntRange getBounds(ServerWorld world, BlockPos pos) {
    return getBounds(world, pos.getX(), pos.getZ());
  }

  private static boolean doesPosAllowSpawning(ServerWorld world, BlockPos pos) {
    if (world.getBiome(pos).isIn(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) return false;
    if (!SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos)) return false;
    if (world.getLightLevel(LightType.BLOCK, pos) > 0) return false;
    if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16.0)) return false;
    return true;
  }

  private static boolean doesEntityCollide(ServerWorld world, Vec3d pos) {
    var boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX(), pos.getY(), pos.getZ());

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  public static boolean canSpawnAtPosBasic(ServerWorld world, BlockPos centerPos, BlockPos pos) {
    var dist = pos.getSquaredDistance(centerPos);
    if (dist < MathHelper.square(16) || dist > MathHelper.square(80)) return false;

    if (!getBounds(world, pos).contains(pos.getY())) return false;
    if (!doesPosAllowSpawning(world, pos)) return false;

    var spawnPos = pos.toBottomCenterPos();
    return doesEntityCollide(world, spawnPos);
  }
}
