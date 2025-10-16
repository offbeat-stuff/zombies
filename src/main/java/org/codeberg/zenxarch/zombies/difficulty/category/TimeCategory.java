package org.codeberg.zenxarch.zombies.difficulty.category;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.chunk.Chunk;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.DifficultyCategory;
import org.codeberg.zenxarch.zombies.difficulty.entry.DifficultyEntry;

public abstract class TimeCategory {
  private static final int TICKS_PER_HOUR = 60 * 60 * 20;
  private static final int TICKS_PER_DAY = 20 * 60 * 20;

  public static final DifficultyEntry DAYS_ENTRY =
      new DifficultyEntry() {
        @Override
        public int getWeight() {
          return 1;
        }

        @Override
        public double calculate(ServerWorld world, Chunk chunk) {
          var dayFactor = mapDays(world.getDifficulty(), getDays(world));
          var moonSize = (double) world.getMoonSize();
          return dayFactor * (1.0 + moonSize);
        }
      };

  public static final DifficultyEntry HOURS_ENTRY =
      new DifficultyEntry() {
        @Override
        public int getWeight() {
          return 10;
        }

        @Override
        public double calculate(ServerWorld world, Chunk chunk) {
          return mapHours(world.getDifficulty(), getHoursInhabited(world, chunk));
        }
      };

  public static final Identifier DAYS_CATEGORY = Zombies.id("days");
  public static final Identifier TIME_CATEGORY = Zombies.id("time");

  public static void initialize() {
    DifficultyCategory.addDifficultyEntry(DAYS_CATEGORY, DAYS_ENTRY);
    DifficultyCategory.addDifficultyEntry(TIME_CATEGORY, HOURS_ENTRY);
  }

  private static double normalize(double value, double start, double end) {
    return MathHelper.clampedMap(value, start, end, 0.0, 1.0);
  }

  private static double mapDays(Difficulty difficulty, double days) {
    return switch (difficulty) {
      case PEACEFUL -> 0.0;
      case EASY -> normalize(days, 5.0, 250.0);
      case NORMAL -> normalize(days, 2.0, 150.0);
      case HARD -> normalize(days, 0.0, 100.0);
    };
  }

  private static double mapHours(Difficulty difficulty, double hours) {
    var delta =
        switch (difficulty) {
          case PEACEFUL -> 0.0;
          case EASY -> normalize(hours, 8.0, 1.5);
          case NORMAL -> normalize(hours, 20.0, 1.5);
          case HARD -> normalize(hours, 35.0, 1.5);
        };
    return MathHelper.lerp(delta, 0.25, 1.0);
  }

  public static double getDays(ServerWorld world) {
    return (double) world.getTimeOfDay() / TICKS_PER_DAY;
  }

  public static double getHoursInhabited(ServerWorld world, Chunk chunk) {
    return chunk.getInhabitedTime() / TICKS_PER_HOUR;
  }
}
