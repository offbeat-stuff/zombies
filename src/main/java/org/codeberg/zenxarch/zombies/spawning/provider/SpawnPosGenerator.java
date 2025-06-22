package org.codeberg.zenxarch.zombies.spawning.provider;


import com.google.common.collect.AbstractIterator;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

public record SpawnPosGenerator(PosModifier modifier) {
  static final int SPAWN_RANGE = 80;

  public static SpawnPosGenerator RANDOM =
      new SpawnPosGenerator(
          (pos, world, random, center) ->
              pos.set(
                  around(random, center.getX(), SPAWN_RANGE),
                  around(random, center.getY(), SPAWN_RANGE),
                  around(random, center.getZ(), SPAWN_RANGE)));

  public static SpawnPosGenerator SURFACE =
      new SpawnPosGenerator(
          (pos, world, random, center) ->
              pos.set(
                  around(random, center.getX(), SPAWN_RANGE),
                  world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, center),
                  around(random, center.getZ(), SPAWN_RANGE)));

  public Iterable<BlockPos> iterator(
      ServerWorld world, Random random, BlockPos centerPos, int count) {
    return () ->
        new AbstractIterator<BlockPos>() {
          final BlockPos.Mutable pos = new BlockPos.Mutable();
          int remaining = count;

          @Override
          protected BlockPos computeNext() {
            if (remaining == 0) return this.endOfData();
            remaining--;
            modifier.modify(pos, world, random, centerPos);
            return pos.toImmutable();
          }
        };
  }

  private static int around(Random random, int center, int range) {
    return center + random.nextBetween(-range, range);
  }

  @FunctionalInterface
  public static interface PosModifier {
    public void modify(BlockPos.Mutable pos, ServerWorld world, Random random, BlockPos center);
  }
}
