package org.codeberg.zenxarch.zombies.info;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class WorldInfo {

  public static int getBlockLightAt(ServerWorld world, BlockPos pos) {
    return world.getLightLevel(LightType.BLOCK, pos);
  }

  public static int getSkyLightAt(ServerWorld world, BlockPos pos) {
    return world.getLightLevel(LightType.SKY, pos);
  }

  public static boolean isOverworld(ServerWorld world) {
    return world.getRegistryKey().equals(World.OVERWORLD);
  }

  public static boolean isNether(ServerWorld world) {
    return world.getRegistryKey().equals(World.NETHER);
  }

  public static boolean isEnd(ServerWorld world) {
    return world.getRegistryKey().equals(World.END);
  }

  public static Difficulty getDifficulty(ServerWorld world) {
    return world.getLevelProperties().getDifficulty();
  }
}
