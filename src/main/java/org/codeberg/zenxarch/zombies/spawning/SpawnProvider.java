package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.random.IntRange;

public abstract class SpawnProvider {
  private static final Random random = Random.create();
  private static final int SPAWN_RANGE = 80;

  private static Optional<BlockPos> validate(
      ServerWorld world, BlockPos centerPos, Optional<BlockPos> pos) {
    return pos.filter(pz -> SpawnUtils.canSpawnAtPosBasic(world, centerPos, pz));
  }

  private static boolean doesNotBlockMob(ServerWorld world, BlockPos pos) {
    return world.getBlockState(pos).getCollisionShape(world, pos).isEmpty();
  }

  private static boolean doesNotContainFluid(ServerWorld world, BlockPos pos) {
    return world.getFluidState(pos).isEmpty();
  }

  private static Optional<BlockPos> adjustPos(ServerWorld world, BlockPos ipos, IntRange yRange) {
    var pos = ipos.mutableCopy();

    var ourRange = IntRange.of(pos.getY(), pos.getY() + 16);
    ourRange = ourRange.intersection(yRange);

    if (!ourRange.isValid()) return Optional.empty();

    for (var y : ourRange.iterate()) {
      pos.setY(y + 1);
      if (!doesNotBlockMob(world, pos)) return Optional.of(pos.up());
      if (!doesNotContainFluid(world, pos)) return Optional.empty();
    }

    return Optional.empty();
  }

  public static Optional<BlockPos> giveSpawnPositions(ServerWorld world, BlockPos centerPos) {
    for (var pos : BlockPos.iterateRandomly(random, 20, centerPos, SPAWN_RANGE)) {
      var range = SpawnUtils.getBounds(world, pos);
      var otherRange = IntRange.around(centerPos.getY(), SPAWN_RANGE);

      range = range.intersection(otherRange);

      if (!range.isValid()) continue;

      var nextPos = adjustPos(world, pos, range);
      nextPos = validate(world, centerPos, nextPos);
      if (nextPos.isPresent()) return nextPos;
    }
    return Optional.empty();
  }
}
