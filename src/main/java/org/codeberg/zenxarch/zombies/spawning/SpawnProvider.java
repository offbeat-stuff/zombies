package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public abstract class SpawnProvider {
  private static final Random random = Random.create();

  private static boolean canSpawnAtPosBasic(ServerWorld world, BlockPos centerPos, BlockPos pos) {
    var dist = pos.getSquaredDistance(centerPos);
    if (dist < MathHelper.square(16) || dist > MathHelper.square(80)) return false;
    if (pos.getY() < world.getBottomY()) return false;
    if (pos.getY()
        > world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()))
      return false;
    if (world.getBiome(pos).isIn(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) return false;
    if (!SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos)) return false;
    if (world.getLightLevel(LightType.BLOCK, pos) > 0) return false;
    if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16.0)) return false;

    var spawnPos = pos.toBottomCenterPos();
    var boundingBox =
        EntityType.ZOMBIE.getSpawnBox(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  public static Optional<BlockPos> giveSpawnPositions(
      ServerWorld world, BlockPos centerPos, double difficulty) {
    var range = 80;
    for (var pos :
        BlockPos.iterateRandomly(
            random, ExtendedDifficulty.getSpawnTries(difficulty), centerPos, range))
      if (canSpawnAtPosBasic(world, centerPos, pos)) return Optional.of(pos);
    return Optional.empty();
  }
}
