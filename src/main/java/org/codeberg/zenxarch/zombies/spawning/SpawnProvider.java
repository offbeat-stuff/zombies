package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;

public class SpawnProvider {
  private final Random random = Random.create();
  private int ticks = 0;
  private int posChecks = 0;
  private int success = 0;

  private static boolean canSpawnAtBlockPos(ServerWorld world, BlockPos pos) {
    if (pos.getY() < world.getBottomY()) return false;
    if (pos.getY()
        > world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()))
      return false;
    if (world.getBiome(pos).isIn(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) return false;
    if (!SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos)) return false;
    if (world.getLightLevel(LightType.BLOCK, pos) > 0) return false;
    if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16.0)) return false;
    return true;
  }

  private static boolean canSpawnAtPos(ServerWorld world, Vec3d pos) {
    var boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX(), pos.getY(), pos.getZ());

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  private static boolean canSpawnAtPosBasic(ServerWorld world, BlockPos centerPos, BlockPos pos) {
    var dist = pos.getSquaredDistance(centerPos);
    if (dist < MathHelper.square(16) || dist > MathHelper.square(80)) return false;

    if (!canSpawnAtBlockPos(world, pos)) return false;

    var spawnPos = pos.toBottomCenterPos();
    return canSpawnAtPos(world, spawnPos);
  }

  private static final int RESET_TICKS = 20 * 20;
  private static final int SPAWN_RANGE = 80;

  private void reset() {
    this.ticks = 0;
    this.posChecks = 0;
    this.success = 0;
  }

  private int getSpawnTries() {
    if (success == 0) return 25;
    return MathHelper.clamp(posChecks / success, 25, 100);
  }

  public Optional<BlockPos> giveSpawnPositions(
      ServerWorld world, BlockPos centerPos, int target, int current) {
    ticks++;
    if (ticks == RESET_TICKS) reset();
    for (var pos : BlockPos.iterateRandomly(this.random, getSpawnTries(), centerPos, SPAWN_RANGE)) {
      posChecks++;
      if (canSpawnAtPosBasic(world, centerPos, pos)) {
        success++;
        return Optional.of(pos);
      }
    }
    return Optional.empty();
  }
}
