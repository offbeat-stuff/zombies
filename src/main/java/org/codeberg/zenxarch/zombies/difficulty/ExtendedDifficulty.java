package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;

public class ExtendedDifficulty {
  private static final Random random = Random.create();

  private static double mapToCurve(double input) {
    return (Math.pow((input + 1), -2) - 1) * (-4.0 / 3.0);
  }

  private static <T extends Item> Item getItemForList(
      Pair<List<T>, List<T>> list, boolean lowerHalf) {
    var itemList = lowerHalf ? list.getLeft() : list.getRight();
    var randomValue = random.nextDouble();
    for (int index = itemList.size() - 1; index >= 0; index--) {
      var chance = mapToCurve((double) index / itemList.size());
      if (randomValue < chance) {
        return itemList.get(index);
      }
    }
    return itemList.get(0);
  }

  private static Item getItemForSlot(EquipmentSlot slot, boolean lowerHalf) {
    return switch (slot) {
      case HEAD -> getItemForList(Equipment.HEAD, lowerHalf);
      case CHEST -> getItemForList(Equipment.CHEST, lowerHalf);
      case LEGS -> getItemForList(Equipment.LEGS, lowerHalf);
      case FEET -> getItemForList(Equipment.FEET, lowerHalf);
      case MAINHAND -> getItemForList(Equipment.SWORD, lowerHalf);
      default -> Items.AIR;
    };
  }

  public static int getTriesForSpawning(ExtendedDifficultyInfo info) {
    if (random.nextDouble() < (1.0 - info.skylight())) {
      return 0;
    }

    return info.period().getSpawnTries(info.progress());
  }

  public static Optional<Item> getEquipmentForSlot(
      ExtendedDifficultyInfo info, EquipmentSlot slot) {
    var index = info.period().ordinal();
    if (index == 0) {
      return Optional.empty();
    }

    if (slot.equals(EquipmentSlot.OFFHAND)) {
      var shieldChance = info.period().getShieldChance(info.progress());

      if (random.nextDouble() < shieldChance) {
        return Optional.of(Items.SHIELD);
      }

      return Optional.empty();
    }

    var commonChance = info.period().getCommonEquipment(info.progress());

    if (random.nextDouble() < commonChance) {
      return Optional.of(getItemForSlot(slot, true));
    }

    var rareChance = info.period().getRareEquipment(info.progress());

    if (random.nextDouble() < rareChance) {
      return Optional.of(getItemForSlot(slot, false));
    }

    return Optional.empty();
  }

  public static ItemStack enchant(
      ServerWorldAccess world, ExtendedDifficultyInfo info, ItemStack input) {
    var index = info.period().ordinal();
    if (index == 0) {
      return input;
    }

    var level = info.period().getEnchantLevel(info.progress() * random.nextDouble());

    var enchantments =
        world
            .getRegistryManager()
            .get(RegistryKeys.ENCHANTMENT)
            .getOrCreateEntryList(EnchantmentTags.NON_TREASURE)
            .stream();
    if (info.period().getTreasure()) {
      enchantments =
          Stream.concat(
              enchantments,
              world
                  .getRegistryManager()
                  .get(RegistryKeys.ENCHANTMENT)
                  .getOrCreateEntryList(EnchantmentTags.TREASURE)
                  .stream());
    }
    return EnchantmentHelper.enchant(random, input, level, enchantments);
  }
}
