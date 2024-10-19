package org.codeberg.zenxarch.zombies.difficulty;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.data.ItemAttributeUtils;
import org.codeberg.zenxarch.zombies.data.ItemSelector;

public record ItemGenerator(List<Item> entries, EquipmentSlot slot, ItemSelector selector) {

  @SafeVarargs
  public static ItemGenerator fromTag(
      EquipmentSlot slot, ItemSelector selector, TagKey<Item>... tags) {
    var stream =
        Stream.of(tags)
            .flatMap(
                tag ->
                    Registries.ITEM.getOrCreateEntryList(tag).stream().map(RegistryEntry::value));
    var list = getList(stream, slot);
    return new ItemGenerator(list, slot, selector);
  }

  public static ItemGenerator fromItem(Item item, EquipmentSlot slot, ItemSelector selector) {
    return new ItemGenerator(List.of(item), slot, selector);
  }

  private static List<Item> getList(Stream<Item> stream, EquipmentSlot slot) {
    return stream
        .filter(f -> matchesSlot(f, slot))
        .sorted(Comparator.comparingDouble(ItemGenerator::score))
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

  public static double scoreWeapon(Item item) {
    var damageBonus = ItemAttributeUtils.getZombieAttackDamage(item);
    var rarityBonus = getRarity(item).ordinal() * 5.0;
    var enchantabilityBonus = item.getEnchantability() / 15.0;
    return damageBonus + rarityBonus + enchantabilityBonus;
  }

  public static double scoreArmor(ArmorItem armor) {
    var slot = armor.getSlotType();
    return ItemAttributeUtils.getZombieArmor(armor, slot)
        + ItemAttributeUtils.getZombieArmorToughness(armor, slot)
        + ItemAttributeUtils.getZombieKnockbackResistance(armor, slot);
  }

  public static double score(Item item) {
    return switch (item) {
      case ArmorItem armor -> scoreArmor(armor);
      default -> scoreWeapon(item);
    };
  }

  public static boolean matchesSlot(Item item, EquipmentSlot slot) {
    return switch (item) {
      case net.minecraft.item.Equipment equ -> equ.getSlotType().equals(slot);
      default -> slot.getType().equals(EquipmentSlot.Type.HAND);
    };
  }
}
