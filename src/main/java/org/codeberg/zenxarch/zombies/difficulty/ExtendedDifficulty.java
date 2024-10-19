package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.TIME_CONFIG;

import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.random.LerpUtils;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

public class ExtendedDifficulty {
  private static final int TICKS_PER_HOUR = 60 * 60 * 20;
  private static final int TICKS_PER_DAY = 20 * 60 * 20;

  public static DoubleDoublePair getTimeFactor(World world, BlockPos pos) {
    double inhibitedHours = 0.0;
    double moonSize = 0.0;
    double days = 0.0;

    if (world.isChunkLoaded(
        ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double) world.getMoonSize();
      inhibitedHours = (double) world.getWorldChunk(pos).getInhabitedTime() / TICKS_PER_HOUR;
    }

    days = (double) world.getTimeOfDay() / TICKS_PER_DAY;

    return DoubleDoublePair.of(days, inhibitedHours * 1.5 * (1.0 + moonSize));
  }

  public static double getDifficulty(World world, BlockPos pos) {
    var time = getTimeFactor(world, pos);
    var difficulty =
        switch (world.getDifficulty()) {
          default -> TIME_CONFIG.EASY.getDifficulty(time);
          case NORMAL -> TIME_CONFIG.NORMAL.getDifficulty(time);
          case HARD -> TIME_CONFIG.HARD.getDifficulty(time);
        };
    return difficulty;
  }

  private static final Random random = Random.create();

  private static double getRandomVariable(
      List<Double> avgl, List<Double> spreadl, double progress) {
    var avg = LerpUtils.lerp(avgl, progress);
    var spread = LerpUtils.lerp(spreadl, progress);
    return RandomUtils.nextDoubleAround(random, avg, spread);
  }

  private static List<Double> spawnTriesAvg = List.of(0.0, 8.0);
  private static List<Double> spawnTriesSpread = List.of(0.0, 10.0);

  public static int getExtraTries(double difficulty) {
    return (int) getRandomVariable(spawnTriesAvg, spawnTriesSpread, difficulty);
  }

  public static int getMaxExtraSuccessfulTries(double difficulty) {
    return Math.round(
        LerpUtils.lerp(List.of(0.0, 4.0), difficulty * random.nextDouble()).floatValue());
  }

  private static List<Double> enchantChance = List.of(0.0, 0.0, 0.025, 0.05);
  private static List<Double> enchantLevelAvg = List.of(0.0, 5.0, 12.5, 28.5);
  private static List<Double> enchantLevelSpread = List.of(0.0, 2.0, 7.5, 2.5);

  public static int getEnchantLevel(double difficulty) {
    return RandomUtils.nextBoolean(random, LerpUtils.lerp(enchantChance, difficulty))
        ? 0
        : (int) getRandomVariable(enchantLevelAvg, enchantLevelSpread, difficulty);
  }

  private static List<Double> equipmentChance = List.of(0.0, 0.5);

  public static boolean shouldSpawnWithEquipment(double difficulty) {
    var atLeastOnce = LerpUtils.lerp(equipmentChance, difficulty);
    var chance = 1.0 - Math.pow(1.0 - atLeastOnce, 1.0 / 6.0);
    return RandomUtils.nextBoolean(random, chance);
  }
}
