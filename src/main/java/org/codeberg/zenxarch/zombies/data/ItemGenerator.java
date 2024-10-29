package org.codeberg.zenxarch.zombies.data;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;

public record ItemGenerator(List<Item> entries, EquipmentSlot slot, ItemSelector selector) {

  public static ItemGenerator fromTag(EquipmentSlot slot, ItemSelector selector, TagKey<Item> tag) {
    var registry = Registries.ITEM;
    var stream = registry.getOrCreateEntryList(tag).stream().map(RegistryEntry::value);
    var list = getList(stream, slot);
    return new ItemGenerator(list, slot, selector);
  }

  private static List<Item> getList(Stream<Item> stream, EquipmentSlot slot) {
    return stream
        .filter(f -> matchesSlot(f, slot))
        .sorted(Comparator.comparingDouble(ItemGenerator::zombieScore))
        .distinct()
        .toList();
  }

  public Item generate(Random random, double difficulty) {
    var index = this.selector.apply(this.entries, random, difficulty);
    if (index == -1) return null;
    return this.entries.get(index);
  }

  public static Rarity getRarity(Item item) {
    return item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
  }

  private static double zombieScoreWeapon(Item item) {
    return ItemAttributeUtils.getZombieAttribute(item, EntityAttributes.GENERIC_ATTACK_DAMAGE);
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
