package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public record ExtendedDifficultyInfo(Period period, double progress,
                                     double skylight, boolean isDay) {

  private static double getTimeFactor(World world, BlockPos pos) {
    double inhibitedHours = 0.0;
    double moonSize = 0.0;
    double days = 0.0;
    if (world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()),
                            ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double)world.getMoonSize();
      inhibitedHours =
          (double)world.getWorldChunk(pos).getInhabitedTime() / (60 * 60 * 20);
    }

    days = (double)world.getTimeOfDay() / 24000.0;

    return (days * 0.5) + (inhibitedHours * 1.5 * (1.0 + moonSize));
  }

  private static Period getTimePeriod(double timeFactor,
                                      PeriodSize periodSize) {
    if (timeFactor < periodSize.grace) {
      return Period.GRACE;
    } else if (timeFactor < periodSize.easy) {
      return Period.EASY;
    } else if (timeFactor < periodSize.hard) {
      return Period.HARD;
    }
    return Period.NIGHTMARE;
  }

  private static double getProgress(double timeFactor, PeriodSize periodSize) {
    var start = periodSize.hard;
    var end = periodSize.nightmare;
    if (timeFactor < periodSize.grace) {
      start = 0;
      end = periodSize.grace;
    } else if (timeFactor < periodSize.easy) {
      start = periodSize.grace;
      end = periodSize.easy;
    } else if (timeFactor < periodSize.hard) {
      start = periodSize.easy;
      end = periodSize.hard;
    }

    return MathHelper.clamp(MathHelper.getLerpProgress(timeFactor, start, end),
                            0.0, 1.0);
  }

  public ExtendedDifficultyInfo(double timeFactor, PeriodSize periodSize,
                                World world, BlockPos pos) {
    this(getTimePeriod(timeFactor, periodSize),
         getProgress(timeFactor, periodSize),
         (double)world.getLightLevel(LightType.SKY, pos) / 15.0, world.isDay());
  }

  public ExtendedDifficultyInfo(World world, BlockPos pos) {
    this(getTimeFactor(world, pos),
         PeriodSize.getFromDifficulty(world.getDifficulty()), world, pos);
  }

  private static enum PeriodSize {
    EASY(20, 50, 200, 1000),
    NORMAL(10, 50, 100, 500),
    HARD(5, 20, 50, 250);

    public final double grace;
    public final double easy;
    public final double hard;
    public final double nightmare;
    private PeriodSize(double grace, double easy, double hard,
                       double nightmare) {
      this.grace = grace;
      this.easy = easy;
      this.hard = hard;
      this.nightmare = nightmare;
    }

    public static PeriodSize getFromDifficulty(Difficulty difficulty) {
      switch (difficulty) {
      case HARD:
        return HARD;
      case NORMAL:
        return NORMAL;
      default:
        return EASY;
      }
    }
  }
}
