package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.helper.LerpImpl;
import org.codeberg.zenxarch.zombies.helper.ProbabilityImpl;

public abstract class Period {
  private static double getRandomVariable(
      Random random, List<Double> avgl, List<Double> spreadl, double progress) {
    var avg = LerpImpl.lerp(avgl, progress);
    var spread = LerpImpl.lerp(spreadl, progress);
    return ProbabilityImpl.nextDoubleAround(random, avg, spread);
  }

  private static List<Double> spawnTriesAvg = List.of(0.0, 15.0);
  private static List<Double> spawnTriesSpread = List.of(0.0, 10.0);

  public static int getSpawnTries(Random random, double difficulty) {
    return 1 + (int) getRandomVariable(random, spawnTriesAvg, spawnTriesSpread, difficulty);
  }

  private static List<Double> enchantChance = List.of(0.0, 0.0, 0.025, 0.05);
  private static List<Double> enchantLevelAvg = List.of(0.0, 5.0, 12.5, 28.5);
  private static List<Double> enchantLevelSpread = List.of(0.0, 2.0, 7.5, 2.5);

  public static int getEnchantLevel(Random random, double difficulty) {
    return ProbabilityImpl.nextBoolean(random, LerpImpl.lerp(enchantChance, difficulty))
        ? 0
        : (int) getRandomVariable(random, enchantLevelAvg, enchantLevelSpread, difficulty);
  }

  private static List<Double> equipmentChance = List.of(0.0, 0.5);

  public static boolean shouldSpawnWithEquipment(Random random, double difficulty) {
    var atLeastOnce = LerpImpl.lerp(equipmentChance, difficulty);
    var chance = 1.0 - Math.pow(1.0 - atLeastOnce, 1.0 / 5.0);
    return ProbabilityImpl.nextBoolean(random, chance);
  }

  private static List<Double> shieldChance = List.of(0.0, 0.0, 0.0, 0.01, 0.05, 0.1);

  public static boolean shouldEquipShield(Random random, double difficulty) {
    var chance = LerpImpl.lerp(shieldChance, difficulty);
    return ProbabilityImpl.nextBoolean(random, chance);
  }
}
