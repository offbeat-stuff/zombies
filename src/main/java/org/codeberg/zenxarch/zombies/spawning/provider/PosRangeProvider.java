package org.codeberg.zenxarch.zombies.spawning.provider;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import org.codeberg.zenxarch.zombies.math.IntRange;
import org.codeberg.zenxarch.zombies.spawning.SpawnUtils;

@FunctionalInterface
public interface PosRangeProvider {
  static final int SPAWN_RANGE = 80;

  public IntRange get(ServerWorld world, BlockPos pos, BlockPos centerPos);

  public static final PosRangeProvider ZERO_BLOCKLIGHT =
      (world, pos, center) -> {
        var mutable = pos.mutableCopy();
        while (mutable.getY() >= (pos.getY() - 16)) {
          if (world.getLightLevel(LightType.BLOCK, mutable) >= 2) mutable.move(Direction.DOWN, 2);
          else if (world.getLightLevel(LightType.BLOCK, mutable) == 1) mutable.move(Direction.DOWN);
          else break;
        }

        var startY = mutable.getY();
        while (mutable.getY() >= (pos.getY() - 16)
            && world.getLightLevel(LightType.BLOCK, mutable) == 0) mutable.move(Direction.DOWN);

        return IntRange.of(mutable.getY(), startY);
      };

  public static final PosRangeProvider AROUND_CENTER =
      (world, pos, center) -> {
        int relx = center.getX() - pos.getX();
        int relz = center.getZ() - pos.getZ();
        var result = getSpawnRangeFor(relx, relz);

        if (!result.isValid()) return IntRange.INVALID;
        result = result.shiftBy(center.getY());
        return result;
      };

  public static final PosRangeProvider HEIGHTMAP_BOUNDS =
      (world, pos, center) -> SpawnUtils.getBounds(world, pos);

  private static IntRange getSpawnRangeFor(int x, int z) {
    int sqDist = x * x + z * z;
    int radSq = SPAWN_RANGE * SPAWN_RANGE;
    if (sqDist >= radSq) return IntRange.INVALID;

    int y = MathHelper.ceil(Math.sqrt((double) radSq - sqDist));
    return IntRange.of(y);
  }
}
