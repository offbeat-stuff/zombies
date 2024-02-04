package org.codeberg.zenxarch.zombies.difficulty;

import java.util.Arrays;
import java.util.List;
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
    } else if (timeFactor < periodSize.hard) {
      start = periodSize.grace;
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

  public static enum Period {
    GRACE,
    HARD,
    NIGHTMARE;

    public static List<Integer> spawnTries = List.of(1, 1, 5, 25);
    public static List<Double> commonEquipment = List.of(0.0, 0.0, 0.1, 0.5);
    public static List<Double> rareEquipment = List.of(0.0, 0.0, 0.0, 0.1);
    public static List<Double> shieldChance = List.of(0.0, 0.0, 0.0, 0.1);
    public static List<Integer> enchantLevel = List.of(0, 0, 20, 40);

    public static List<Boolean> treasure = List.of(false, false, true);

    public static int indexOf(Period period) {
      var index = Arrays.binarySearch(Period.values(), period);
      index = index == -1 ? 0 : index;
      return index;
    }
  }

  private static enum PeriodSize {
    EASY(25, 200, 1000),
    NORMAL(10, 100, 500),
    HARD(5, 50, 250);

    public final double grace;
    public final double hard;
    public final double nightmare;
    private PeriodSize(double grace, double hard, double nightmare) {
      this.grace = grace;
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
