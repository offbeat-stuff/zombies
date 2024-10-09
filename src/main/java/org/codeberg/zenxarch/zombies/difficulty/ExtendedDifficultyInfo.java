package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.TIME_CONFIG;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.helper.LerpImpl;

public abstract class ExtendedDifficultyInfo {
  private static double getTimeFactor(World world, BlockPos pos) {
    double inhibitedHours = 0.0;
    double moonSize = 0.0;
    double days = 0.0;
    if (world.isChunkLoaded(
        ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double) world.getMoonSize();
      inhibitedHours = (double) world.getWorldChunk(pos).getInhabitedTime() / (60 * 60 * 20);
    }

    days = (double) world.getTimeOfDay() / 24000.0;

    return (days * 0.5) + (inhibitedHours * 1.5 * (1.0 + moonSize));
  }

  private static double mapTimeFactor(double difficulty) {
    return LerpImpl.lerp(List.of(0.0, 0.1, 0.5, 0.9, 1.0), difficulty);
  }

  public static double getDifficulty(World world, BlockPos pos) {
    var time = getTimeFactor(world, pos);
    var difficulty =
        switch (world.getDifficulty()) {
          default -> TIME_CONFIG.EASY.getDifficulty(time);
          case NORMAL -> TIME_CONFIG.NORMAL.getDifficulty(time);
          case HARD -> TIME_CONFIG.HARD.getDifficulty(time);
        };
    return mapTimeFactor(difficulty);
  }
}
