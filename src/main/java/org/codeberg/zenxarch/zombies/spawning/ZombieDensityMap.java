package org.codeberg.zenxarch.zombies.spawning;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public final class ZombieDensityMap {

  private static final int BOX_SIZE = 24;
  private static final int MAX_DISTANCE_FROM_CENTER = 3;

  private static int round(int v) {
    return v / BOX_SIZE;
  }

  private static Vec3i round(BlockPos pos) {
    var x = round(pos.getX());
    var y = round(pos.getY());
    var z = round(pos.getZ());
    return new Vec3i(x, y, z);
  }

  public static void push(Object2IntMap<Vec3i> map, BlockPos element) {
    var key = round(element);
    map.put(key, map.getOrDefault(key, 0) + 1);
  }

  public static Object2IntMap<Vec3i> getSubMap(Object2IntMap<Vec3i> map, BlockPos pos) {
    var result = new Object2IntArrayMap<Vec3i>();
    for (var key : map.keySet()) if (isWithinDistance(key, pos)) result.put(key, map.getInt(key));
    return result;
  }

  public static int get(Object2IntMap<Vec3i> map, BlockPos pos) {
    return map.getInt(round(pos));
  }

  private static boolean isWithinDistance(Vec3i box, BlockPos pos) {
    var mapped = round(pos);
    return isWithinDistance(box.getX(), mapped.getX())
        && isWithinDistance(box.getY(), mapped.getY())
        && isWithinDistance(box.getZ(), mapped.getZ());
  }

  private static boolean isWithinDistance(int a, int b) {
    return Math.abs(a - b) <= MAX_DISTANCE_FROM_CENTER;
  }

  public static int getMaxDensity(int toSpawn) {
    return Math.max(toSpawn / 49, 1) * 8;
  }
}
