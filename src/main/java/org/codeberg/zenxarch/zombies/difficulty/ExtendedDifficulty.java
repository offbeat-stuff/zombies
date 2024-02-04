package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficultyInfo.Period;

public class ExtendedDifficulty {
  private static final Random random = Random.create();

  private static double mapToCurve(double input) {
    return (Math.pow((input + 1), -2) - 1) * (-4.0 / 3.0);
  }

  private static <T extends Item>
      Item getItemForList(Pair<List<T>, List<T>> list, boolean lowerHalf) {
    var f = lowerHalf ? list.getLeft() : list.getRight();
    var r = random.nextDouble();
    for (int i = f.size() - 1; i >= 0; i--) {
      var x = mapToCurve((double)i / f.size());
      if (r < x) {
        return f.get(i);
      }
    }
    return f.get(0);
  }

  private static Item getItemForSlot(EquipmentSlot slot, boolean lowerHalf) {
    switch (slot) {
    case HEAD:
      return getItemForList(Equipment.HEAD, lowerHalf);
    case CHEST:
      return getItemForList(Equipment.CHEST, lowerHalf);
    case LEGS:
      return getItemForList(Equipment.LEGS, lowerHalf);
    case FEET:
      return getItemForList(Equipment.FEET, lowerHalf);
    case MAINHAND:
      return getItemForList(Equipment.SWORD, lowerHalf);
    default:
      return Items.AIR;
    }
  }

  public static int getTriesForSpawning(ExtendedDifficultyInfo info) {
    if (random.nextDouble() < (1.0 - info.skylight())) {
      return 0;
    }

    var index = Period.indexOf(info.period());

    return (int)MathHelper.lerp(info.progress(), Period.spawnTries.get(index),
                                Period.spawnTries.get(index + 1));
  }

  public static Optional<Item> getEquipmentForSlot(ExtendedDifficultyInfo info,
                                                   EquipmentSlot slot) {
    var index = Period.indexOf(info.period());
    if (index == 0) {
      return Optional.empty();
    }

    if (slot.equals(EquipmentSlot.OFFHAND)) {
      var shieldChance =
          MathHelper.lerp(info.progress(), Period.shieldChance.get(index),
                          Period.shieldChance.get(index + 1));

      if (random.nextDouble() < shieldChance) {
        return Optional.of(Items.SHIELD);
      }

      return Optional.empty();
    }

    var commonChance =
        MathHelper.lerp(info.progress(), Period.commonEquipment.get(index),
                        Period.commonEquipment.get(index + 1));

    if (random.nextDouble() < commonChance) {
      return Optional.of(getItemForSlot(slot, true));
    }

    var rareChance =
        MathHelper.lerp(info.progress(), Period.rareEquipment.get(index),
                        Period.rareEquipment.get(index + 1));

    if (random.nextDouble() < rareChance) {
      return Optional.of(getItemForSlot(slot, false));
    }

    return Optional.empty();
  }

  public static ItemStack enchant(ExtendedDifficultyInfo info,
                                  ItemStack input) {
    var index = Period.indexOf(info.period());
    if (index == 0) {
      return input;
    }

    var level = (int)MathHelper.lerp(info.progress() * random.nextDouble(),
                                     Period.enchantLevel.get(index),
                                     Period.enchantLevel.get(index + 1));

    return EnchantmentHelper.enchant(random, input, level,
                                     Period.treasure.get(index));
  }
}
