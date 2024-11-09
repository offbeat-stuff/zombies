package org.codeberg.zenxarch.zombies.data;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;

public abstract class ItemGenerator {
  public static List<Item> getList(TagKey<Item> tag, EquipmentSlot slot) {
    var registry = Registries.ITEM;
    return registry.getOptional(tag).stream()
        .flatMap(Named::stream)
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
    return ItemAttributeUtils.getZombieAttribute(item, EntityAttributes.ATTACK_DAMAGE)
        * MathHelper.square(
            ItemAttributeUtils.getZombieAttribute(item, EntityAttributes.ATTACK_SPEED));
  }

  private static Optional<EquipmentSlot> getEquipmentSlot(Item item) {
    if (!item.getComponents().contains(DataComponentTypes.EQUIPPABLE)) return Optional.empty();
    var component = item.getComponents().get(DataComponentTypes.EQUIPPABLE);
    return component.allows(EntityType.ZOMBIE) ? Optional.of(component.slot()) : Optional.empty();
  }

  private static double zombieScoreArmor(ArmorItem armor) {
    var slot = getEquipmentSlot(armor).orElse(EquipmentSlot.MAINHAND);
    return ItemAttributeUtils.getZombieAttribute(armor, EntityAttributes.ARMOR, slot)
        + ItemAttributeUtils.getZombieAttribute(armor, EntityAttributes.ARMOR_TOUGHNESS, slot)
        + ItemAttributeUtils.getZombieAttribute(armor, EntityAttributes.KNOCKBACK_RESISTANCE, slot);
  }

  private static double zombieScore(Item item) {
    return switch (item) {
      case ArmorItem armor -> zombieScoreArmor(armor);
      default -> zombieScoreWeapon(item);
    };
  }

  public static boolean matchesSlot(Item item, EquipmentSlot slot) {
    if (slot.getType().equals(EquipmentSlot.Type.HAND)) return true;
    return slot.equals(getEquipmentSlot(item).orElse(EquipmentSlot.MAINHAND));
  }
}
