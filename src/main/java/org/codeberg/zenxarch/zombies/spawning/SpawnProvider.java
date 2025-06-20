package org.codeberg.zenxarch.zombies.spawning;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.math.IntRange;
import org.codeberg.zenxarch.zombies.spawning.provider.SpawnPosGenerator;

public final class SpawnProvider {
  private static final Random random = Random.create();
  private static final int SPAWN_RANGE = 80;
  private static final int SQ_NEARBY_RANGE = 16 * 16;

  private SpawnProvider() {
    throw new IllegalStateException("Utility class");
  }

  private static boolean isTooCloseToAny(BlockPos b, List<BlockPos> positions) {
    for (var pos : positions) {
      var sqDist = pos.getSquaredDistance(b);
      if (sqDist < SQ_NEARBY_RANGE) return false;
    }
    return true;
  }

  private static Optional<BlockPos> validate(
      ServerWorld world, List<BlockPos> positions, Optional<BlockPos> pos) {
    return pos.filter(b -> isTooCloseToAny(b, positions))
        .filter(pz -> SpawnUtils.canSpawnAtPosBasic(world, pz));
  }

  private static boolean doesNotBlockMob(ServerWorld world, BlockPos pos) {
    return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
  }

  private static boolean doesNotContainFluid(ServerWorld world, BlockPos pos) {
    return world.getFluidState(pos).isEmpty();
  }

  private static Optional<BlockPos> adjustPos(ServerWorld world, BlockPos ipos, IntRange yRange) {
    var pos = ipos.mutableCopy();

    var blockLight = world.getLightLevel(LightType.BLOCK, pos);
    var ourRange = IntRange.of(blockLight, 16).shiftBy(pos.getY());
    ourRange = ourRange.intersection(yRange);

    if (!ourRange.isValid()) return Optional.empty();

    for (var y : ourRange.iterate()) {
      pos.setY(y + 1);
      if (!doesNotBlockMob(world, pos)) return Optional.of(pos.up());
      if (!doesNotContainFluid(world, pos)) return Optional.empty();
    }

    return Optional.empty();
  }

  private static IntRange getSpawnRangeFor(int x, int z) {
    int sqDist = x * x + z * z;
    int radSq = SPAWN_RANGE * SPAWN_RANGE;
    if (sqDist >= radSq) return IntRange.INVALID;

    int y = MathHelper.ceil(Math.sqrt((double) radSq - sqDist));
    return IntRange.of(y);
  }

  private static IntRange getYRange(ServerWorld world, BlockPos centerPos, BlockPos pos) {
    var range = SpawnUtils.getBounds(world, pos);

    int relx = centerPos.getX() - pos.getX();
    int relz = centerPos.getZ() - pos.getZ();
    var otherRange = getSpawnRangeFor(relx, relz);

    if (!otherRange.isValid()) return IntRange.INVALID;
    otherRange = otherRange.shiftBy(centerPos.getY());

    range = range.intersection(otherRange);

    return range;
  }

  public static Optional<BlockPos> giveSpawnPositions(
      ServerWorld world,
      BlockPos centerPos,
      List<BlockPos> positions,
      Object2IntMap<Vec3i> densityMap,
      int toSpawn) {
    var spawnTries =
        (toSpawn * 100) / (world.getGameRules().getInt(ZombieGamerules.SPAWN_SPEED) * 20);
    spawnTries = Math.max(spawnTries, 1);

    var maxDensity = ZombieDensityMap.getMaxDensity(toSpawn);

    for (var pos : SpawnPosGenerator.RANDOM.iterator(world, random, centerPos, toSpawn)) {
      if (SpawnUtils.burnsZombie(world, pos) || ZombieDensityMap.get(densityMap, pos) > maxDensity)
        continue;

      var adjustedPos = tryAdjustRandomPos(world, centerPos, pos);
      adjustedPos = validate(world, positions, adjustedPos);
      if (adjustedPos.isPresent()) return adjustedPos;
    }
    return Optional.empty();
  }

  private static Optional<BlockPos> tryAdjustRandomPos(
      ServerWorld world, BlockPos origin, BlockPos pos) {
    var range = getYRange(world, origin, pos);
    if (!range.isValid()) return Optional.empty();

    return adjustPos(world, pos, range);
  }
}
