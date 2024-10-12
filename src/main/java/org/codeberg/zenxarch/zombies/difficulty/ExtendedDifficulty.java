package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.EQUIPMENT_CONFIG;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.helper.LerpImpl;
import org.codeberg.zenxarch.zombies.helper.ProbabilityImpl;
import org.jetbrains.annotations.Nullable;

public class ExtendedDifficulty {
  private static final Random random = Random.create();

  private static final List<Double> ITEM_SELECT_CHANCE = List.of(0.9, 0.3);

  private static Item getItemForSlot(EquipmentSlot slot, Random random, double difficulty) {
    var chance = LerpImpl.lerp(ITEM_SELECT_CHANCE, difficulty);
    List<? extends Item> items =
        switch (slot) {
          case HEAD, CHEST, LEGS, FEET -> Equipment.getArmorList(slot);
          case MAINHAND -> Equipment.getWeaponsList();
          default -> List.of();
        };
    if (items.isEmpty()) return null;
    for (var item : items) {
      if (ProbabilityImpl.nextBoolean(random, chance)) return item;
    }
    return items.get(0);
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
      EquipmentSlot slot, boolean shouldSpawnWithEquipment, Random random, double difficulty) {
    return shouldSpawnWithEquipment ? getItemForSlot(slot, random, difficulty) : null;
  }

  public static Optional<Item> getEquipmentForSlot(double difficulty, EquipmentSlot slot) {
    var item =
        switch (slot) {
          case EquipmentSlot.OFFHAND -> offhandItem(Period.shouldEquipShield(random, difficulty));
          default ->
              equipmentItem(
                  slot, Period.shouldSpawnWithEquipment(random, difficulty), random, difficulty);
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
