package org.codeberg.zenxarch.zombies.spawning;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.math.IntRange;
import org.codeberg.zenxarch.zombies.spawning.provider.PosRangeProvider;
import org.codeberg.zenxarch.zombies.spawning.provider.SpawnPosProvider;

public final class SpawnProvider {
  private static final Random random = Random.create();
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

    for (var y : yRange.iterate()) {
      pos.setY(y + 1);
      if (!doesNotBlockMob(world, pos)) return Optional.of(pos.up());
      if (!doesNotContainFluid(world, pos)) return Optional.empty();
    }

    return Optional.empty();
  }

  private static IntRange getYRange(ServerWorld world, BlockPos centerPos, BlockPos pos) {
    final var providers =
        List.of(
            PosRangeProvider.AROUND_CENTER,
            PosRangeProvider.HEIGHTMAP_BOUNDS,
            PosRangeProvider.ZERO_BLOCKLIGHT);
    var ranges = new ArrayList<IntRange>(providers.size());
    for (var provider : providers) ranges.add(provider.get(world, pos, centerPos));

    for (var range : ranges) if (!range.isValid()) return IntRange.INVALID;

    var result = ranges.get(0);
    for (int i = 1; i < ranges.size(); i++) {
      result = result.intersection(ranges.get(i));
      if (!result.isValid()) return IntRange.INVALID;
    }

    return result;
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

    for (var pos : SpawnPosProvider.MIXED.iterate(world, random, centerPos, toSpawn)) {
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
