package org.codeberg.zenxarch.zombies.spawning.provider;

import com.google.common.collect.AbstractIterator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

@FunctionalInterface
public interface SpawnPosProvider {
  static final int SPAWN_RANGE = 80;

  public void modify(BlockPos.Mutable pos, ServerWorld world, Random random, BlockPos center);

  public static SpawnPosProvider RANDOM =
      (pos, world, random, center) ->
          pos.set(
              around(random, center.getX(), SPAWN_RANGE),
              around(random, center.getY(), SPAWN_RANGE),
              around(random, center.getZ(), SPAWN_RANGE));

  public static SpawnPosProvider SURFACE =
      (pos, world, random, center) ->
          setToTopPosAt(
              world,
              around(random, center.getX(), SPAWN_RANGE),
              around(random, center.getZ(), SPAWN_RANGE),
              pos);

  public static SpawnPosProvider MIXED =
      (pos, world, random, center) -> {
        var topY =
            world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, center.getX(), center.getZ());
        var distToSurface = center.getY() + 8 - topY;
        ((distToSurface > 0 && random.nextBoolean()) ? SURFACE : RANDOM)
            .modify(pos, world, random, center);
      };

  public default Iterable<BlockPos> iterate(
      ServerWorld world, Random random, BlockPos centerPos, int count) {
    return () ->
        new AbstractIterator<BlockPos>() {
          final BlockPos.Mutable pos = new BlockPos.Mutable();
          int remaining = count;

          @Override
          protected BlockPos computeNext() {
            if (remaining == 0) return this.endOfData();
            remaining--;
            modify(pos, world, random, centerPos);
            return pos.toImmutable();
          }
        };
  }

  private static int around(Random random, int center, int range) {
    return center + random.nextBetween(-range, range);
  }

  private static void setToTopPosAt(ServerWorld world, int x, int z, BlockPos.Mutable mutable) {
    mutable.set(x, world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z), z);
  }
}
