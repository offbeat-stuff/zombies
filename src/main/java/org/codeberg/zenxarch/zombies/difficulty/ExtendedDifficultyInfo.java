package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public record ExtendedDifficultyInfo(Period period, double progress, boolean isDay) {
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

  public ExtendedDifficultyInfo(
      double timeFactor, PeriodSize periodSize, World world, BlockPos pos) {
    this(periodSize.getTimePeriod(timeFactor), periodSize.getProgress(timeFactor), world.isDay());
  }

  public ExtendedDifficultyInfo(World world, BlockPos pos) {
    this(
        getTimeFactor(world, pos), PeriodSize.getFromDifficulty(world.getDifficulty()), world, pos);
  }

  private static enum PeriodSize {
    EASY(20, 50, 200, 500, 2500),
    NORMAL(10, 50, 100, 250, 500),
    HARD(5, 20, 50, 100, 250);

    public final double[] args;

    private PeriodSize(double... args) {
      assert args.length == Period.values().length;
      this.args = args;
    }

    public Period getTimePeriod(double timeFactor) {
      var returnIndex = 0;
      while (returnIndex < args.length && timeFactor > args[returnIndex]) {
        returnIndex++;
      }
      returnIndex = MathHelper.clamp(returnIndex, 0, Period.values().length - 1);
      return Period.values()[returnIndex];
    }

    public double getProgress(double timeFactor) {
      var start = 0.0;
      var end = args[0];
      var index = 0;
      while ((index + 1) < args.length && timeFactor > end) {
        start = this.args[index];
        end = this.args[index + 1];
        index++;
      }

      return MathHelper.clamp(MathHelper.getLerpProgress(timeFactor, start, end), 0.0, 1.0);
    }

    public static PeriodSize getFromDifficulty(Difficulty difficulty) {
      return switch (difficulty) {
        case HARD -> HARD;
        case NORMAL -> NORMAL;
        default -> EASY;
      };
    }
  }
}
