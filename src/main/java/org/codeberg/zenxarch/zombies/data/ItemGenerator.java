package org.codeberg.zenxarch.zombies.data;

import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;

public abstract class ItemGenerator {
  public static List<Item> getList(TagKey<Item> tag, EquipmentSlot slot) {
    var registry = Registries.ITEM;
    return registry.getOrCreateEntryList(tag).stream()
        .map(RegistryEntry::value)
        .filter(f -> matchesSlot(f, slot))
        .sorted(Comparator.comparingDouble(ItemGenerator::zombieScore))
        .distinct()
        .toList();
  }

  public static Rarity getRarity(Item item) {
    return item.getDefaultStack().getRarity();
  }

  private static double zombieScoreWeapon(Item item) {
    return ItemAttributeUtils.getZombieAttribute(item, EntityAttributes.GENERIC_ATTACK_DAMAGE)
        * MathHelper.square(
            ItemAttributeUtils.getZombieAttribute(item, EntityAttributes.GENERIC_ATTACK_SPEED));
  }

  private static double zombieScoreArmor(ArmorItem armor) {
    var slot = armor.getSlotType();
    return ItemAttributeUtils.getZombieAttribute(armor, EntityAttributes.GENERIC_ARMOR, slot)
        + ItemAttributeUtils.getZombieAttribute(
            armor, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, slot)
        + ItemAttributeUtils.getZombieAttribute(
            armor, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, slot);
  }

  private static double zombieScore(Item item) {
    return switch (item) {
      case ArmorItem armor -> zombieScoreArmor(armor);
      default -> zombieScoreWeapon(item);
    };
  }

  public static boolean matchesSlot(Item item, EquipmentSlot slot) {
    return switch (item) {
      case net.minecraft.item.Equipment equ -> equ.getSlotType().equals(slot);
      default -> slot.getType().equals(EquipmentSlot.Type.HAND);
    };
  }
}
