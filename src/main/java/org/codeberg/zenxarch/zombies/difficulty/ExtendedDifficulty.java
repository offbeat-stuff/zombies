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
import org.jetbrains.annotations.Nullable;

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
    return info.period().getSpawnTries(info.progress());
  }

  @Nullable
  private static Item offhandItem(double chance) {
    return random.nextDouble() < chance ? Items.SHIELD : null;
  }

  @Nullable
  private static Item equipmentItem(EquipmentSlot slot, double commonChance, double rareChance) {
    return random.nextDouble() < commonChance
        ? getItemForSlot(slot, true)
        : random.nextDouble() < rareChance ? getItemForSlot(slot, false) : null;
  }

  public static Optional<Item> getEquipmentForSlot(
      ExtendedDifficultyInfo info, EquipmentSlot slot) {
    var period = info.period();
    var progress = info.progress();
    var index = period.ordinal();
    if (index == 0) return Optional.empty();

    var item =
        switch (slot) {
          case EquipmentSlot.OFFHAND -> offhandItem(period.getShieldChance(progress));
          default ->
              equipmentItem(
                  slot, period.getCommonEquipment(progress), period.getRareEquipment(progress));
        };
    return Optional.ofNullable(item);
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
