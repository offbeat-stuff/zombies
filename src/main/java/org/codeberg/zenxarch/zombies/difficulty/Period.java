package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import org.codeberg.zenxarch.zombies.helper.LerpImpl;

public abstract class Period {
  private static List<Double> spawnTries = List.of(0.0, 1.0, 2.0, 3.0, 5.0, 25.0);

  public static int getSpawnTries(double progress) {
    return LerpImpl.lerp(spawnTries, progress).intValue();
  }

  private static List<Double> enchantLevel = List.of(0.0, 0.0, 5.0, 10.0, 20.0, 40.0);

  public static int getEnchantLevel(double progress) {
    return LerpImpl.lerp(enchantLevel, progress).intValue();
  }

  private static List<Double> commonEquipment = List.of(0.0, 0.0, 0.02, 0.05, 0.1, 0.5);

  public static double getCommonEquipment(double progress) {
    return LerpImpl.lerp(commonEquipment, progress);
  }

  private static List<Double> rareEquipment = List.of(0.0, 0.0, 0.0, 0.01, 0.05, 0.1);

  public static double getRareEquipment(double progress) {
    return LerpImpl.lerp(rareEquipment, progress);
  }

  private static List<Double> shieldChance = List.of(0.0, 0.0, 0.0, 0.01, 0.05, 0.1);

  public static double getShieldChance(double progress) {
    return LerpImpl.lerp(shieldChance, progress);
  }

  private static List<Double> treasure = List.of(-1.0, 0.2);

  public static boolean getTreasure(double progress) {
    return LerpImpl.lerp(treasure, progress) > 0.0;
  }
}
