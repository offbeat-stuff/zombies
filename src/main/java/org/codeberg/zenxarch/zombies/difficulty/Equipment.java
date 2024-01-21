package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;

public class Equipment {

  private static List<Item> getEquipmentListForSlot(EquipmentSlot slot) {
    switch (slot) {
    case HEAD:
      return List.of(Items.LEATHER_HELMET, Items.IRON_HELMET,
                     Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
    case CHEST:
      return List.of(Items.LEATHER_CHESTPLATE, Items.IRON_CHESTPLATE,
                     Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
    case LEGS:
      return List.of(Items.LEATHER_LEGGINGS, Items.IRON_LEGGINGS,
                     Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
    case FEET:
      return List.of(Items.LEATHER_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS,
                     Items.NETHERITE_BOOTS);
    case MAINHAND:
      return List.of(Items.IRON_SHOVEL, Items.IRON_SWORD, Items.DIAMOND_SWORD,
                     Items.NETHERITE_AXE);
    default:
      return List.of(Items.AIR, Items.AIR, Items.AIR, Items.AIR);
    }
  }

  private static Item getEquipmentForSlot(EquipmentSlot slot, double level) {
    var list = getEquipmentListForSlot(slot);
    if (list == null) {
      return null;
    }

    if (level < 0.1) {
      return list.get(0);
    }

    if (level < 0.9) {
      return list.get(1);
    }

    if (level < 0.975) {
      return list.get(2);
    }

    return list.get(3);
  }

  public static void initEquipment(ZombieEntity zombie, double difficulty) {
    var random = zombie.getRandom();

    for (var slot : EquipmentSlot.values()) {
      if (!zombie.getEquippedStack(slot).isEmpty()) {
        continue;
      }

      if (random.nextDouble() > difficulty) {
        continue;
      }

      zombie.equipStack(
          slot, getEquipmentForSlot(slot, difficulty * random.nextDouble())
                    .getDefaultStack());
    }
  }

  public static void updateEnchantments(ZombieEntity zombie,
                                        double difficulty) {
    var random = zombie.getRandom();
    if (!zombie.getMainHandStack().isEmpty() &&
        random.nextDouble() < difficulty) {
      zombie.equipStack(EquipmentSlot.MAINHAND,
                        EnchantmentHelper.enchant(
                            random, zombie.getMainHandStack(),
                            (int)MathHelper.clampedLerp(
                                1.0, 40.0, difficulty + random.nextDouble()),
                            true));
    }

    for (var slot : EquipmentSlot.values()) {
      if (zombie.getEquippedStack(slot).isEmpty()) {
        continue;
      }

      zombie.equipStack(slot,
                        EnchantmentHelper.enchant(
                            random, zombie.getEquippedStack(slot),
                            (int)MathHelper.clampedLerp(
                                1.0, 40.0, difficulty + random.nextDouble()),
                            true));
    }
  }
}
