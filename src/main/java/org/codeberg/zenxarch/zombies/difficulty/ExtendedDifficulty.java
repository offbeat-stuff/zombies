package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.EQUIPMENT_CONFIG;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.helper.ProbabilityImpl;
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
      if (ProbabilityImpl.nextBoolean(randomValue, chance)) {
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

  public static int getTriesForSpawning(double difficulty) {
    return Period.getSpawnTries(random, difficulty);
  }

  @Nullable
  private static Item offhandItem(boolean ok) {
    return ok ? Items.SHIELD : null;
  }

  @Nullable
  private static Item equipmentItem(
      EquipmentSlot slot, boolean shouldSpawnWithEquipment, boolean useRareEquipment) {
    return shouldSpawnWithEquipment ? getItemForSlot(slot, !useRareEquipment) : null;
  }

  public static Optional<Item> getEquipmentForSlot(double difficulty, EquipmentSlot slot) {
    var item =
        switch (slot) {
          case EquipmentSlot.OFFHAND -> offhandItem(Period.shouldEquipShield(random, difficulty));
          default ->
              equipmentItem(
                  slot,
                  Period.shouldSpawnWithEquipment(random, difficulty),
                  Period.useRareEquipment(random, difficulty));
        };
    return Optional.ofNullable(item);
  }

  public static ItemStack enchant(ServerWorldAccess world, double difficulty, ItemStack input) {
    var level = Period.getEnchantLevel(random, difficulty);
    if (level == 0) return input;

    var enchantments = EQUIPMENT_CONFIG.getEnchantments(world, difficulty);
    return EnchantmentHelper.enchant(random, input, level, enchantments);
  }
}
