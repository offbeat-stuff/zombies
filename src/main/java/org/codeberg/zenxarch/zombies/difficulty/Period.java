package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import net.minecraft.util.math.MathHelper;

public enum Period {
  GRACE,
  EASY,
  MEDIUM,
  HARD,
  NIGHTMARE;

  public static List<Integer> spawnTries = List.of(0, 1, 2, 3, 5, 25);

  public int getSpawnTries(double progress) {
    int index = this.ordinal();
    return (int) MathHelper.lerp(progress, spawnTries.get(index), spawnTries.get(index + 1));
  }

  public static List<Integer> enchantLevel = List.of(0, 0, 5, 10, 20, 40);

  public int getEnchantLevel(double progress) {
    int index = this.ordinal();
    return (int) MathHelper.lerp(progress, enchantLevel.get(index), enchantLevel.get(index + 1));
  }

  public static List<Double> commonEquipment = List.of(0.0, 0.0, 0.02, 0.05, 0.1, 0.5);

  public double getCommonEquipment(double progress) {
    int index = this.ordinal();
    return (double)
        MathHelper.lerp(progress, commonEquipment.get(index), commonEquipment.get(index + 1));
  }

  public static List<Double> rareEquipment = List.of(0.0, 0.0, 0.0, 0.01, 0.05, 0.1);

  public double getRareEquipment(double progress) {
    int index = this.ordinal();
    return (double)
        MathHelper.lerp(progress, rareEquipment.get(index), rareEquipment.get(index + 1));
  }

  public static List<Double> shieldChance = List.of(0.0, 0.0, 0.0, 0.01, 0.05, 0.1);

  public double getShieldChance(double progress) {
    int index = this.ordinal();
    return (double) MathHelper.lerp(progress, shieldChance.get(index), shieldChance.get(index + 1));
  }

  public static List<Boolean> treasure = List.of(false, false, false, true, true);

  public boolean getTreasure() {
    return treasure.get(this.ordinal());
  }
}
