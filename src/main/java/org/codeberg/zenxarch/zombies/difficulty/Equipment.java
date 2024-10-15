package org.codeberg.zenxarch.zombies.difficulty;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.helper.ItemAttributesImpl;
import org.codeberg.zenxarch.zombies.helper.LerpImpl;
import org.codeberg.zenxarch.zombies.helper.ProbabilityImpl;

public abstract class Equipment {
  // private static final Comparator<Double> LOOSE_COMPARE =
  // (a, b) -> Math.abs(b - a) < 0.4 ? 0 : Double.compare(a, b);

  private static Rarity getRarity(Item item) {
    return item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
  }

  private static double scoreWeapon(Item item) {
    var damageBonus = ItemAttributesImpl.getZombieAttackDamage(item);
    var rarityBonus = getRarity(item).ordinal() * 5.0;
    var enchantabilityBonus = item.getEnchantability() / 15.0;
    return damageBonus + rarityBonus + enchantabilityBonus;
  }

  private static double scoreArmor(ArmorItem armor) {
    var slot = armor.getSlotType();
    return ItemAttributesImpl.getZombieArmor(armor, slot)
        + ItemAttributesImpl.getZombieArmorToughness(armor, slot)
        + ItemAttributesImpl.getZombieKnockbackResistance(armor, slot);
  }

  private static double score(Item item) {
    return switch (item) {
      case ArmorItem armor -> scoreArmor(armor);
      default -> scoreWeapon(item);
    };
  }

  private static boolean matchesSlot(Item item, EquipmentSlot slot) {
    return switch (item) {
      case net.minecraft.item.Equipment equ -> equ.getSlotType().equals(slot);
      default -> slot.getType().equals(EquipmentSlot.Type.HAND);
    };
  }

  private static Item unwrapEntry(Registry<Item> registry, RegistryEntry<Item> entry) {
    return entry.getKeyOrValue().map(registry::get, a -> a);
  }

  private static Stream<Item> toStream(TagKey<Item> tag) {
    var registry = Registries.ITEM;
    var entries = registry.getOrCreateEntryList(tag);
    return entries.stream().map(v -> unwrapEntry(registry, v));
  }

  private static Stream<Item> toStream(List<TagKey<Item>> tags) {
    return tags.stream().flatMap(Equipment::toStream);
  }

  private static List<Item> sortStream(Stream<Item> stream, EquipmentSlot slot) {
    return stream
        .filter(v -> matchesSlot(v, slot))
        .sorted(Comparator.comparingDouble(Equipment::score))
        .distinct()
        .toList();
  }

  private static List<Item> sortStream(Stream<Item> stream) {
    return sortStream(stream, EquipmentSlot.MAINHAND);
  }

  private static <T> T getRandomItemBasedOnChance(List<T> list, Random random, double chance) {
    if (list.isEmpty()) return null;
    for (var v : list) if (ProbabilityImpl.nextBoolean(random, chance)) return v;
    return list.get(0);
  }

  public static Item getRandomWeapon(Random random, double difficulty) {
    var axes = sortStream(toStream(ItemTags.AXES));
    var swords = sortStream(toStream(ItemTags.SWORD_ENCHANTABLE));
    var special =
        sortStream(toStream(List.of(ItemTags.TRIDENT_ENCHANTABLE, ItemTags.MACE_ENCHANTABLE)));
    var selected = LerpImpl.lerpWeighted(random, 1.0, 250.0, 3);
    if (selected == 0) return getRandomItemBasedOnChance(special, random, 0.3);
    return LerpImpl.lerpWeighted(
        random,
        LerpImpl.lerp(List.of(1000.0, 100.0), difficulty),
        1.0,
        selected == 1 ? axes : swords);
  }

  private static List<Item> getArmorList(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> sortStream(toStream(ItemTags.HEAD_ARMOR_ENCHANTABLE), slot);
      case CHEST -> sortStream(toStream(ItemTags.CHEST_ARMOR_ENCHANTABLE), slot);
      case LEGS -> sortStream(toStream(ItemTags.LEG_ARMOR_ENCHANTABLE), slot);
      case FEET -> sortStream(toStream(ItemTags.FOOT_ARMOR_ENCHANTABLE), slot);
      default -> List.of();
    };
  }

  private static final List<Double> ARMOR_SELECT_CHANCE = List.of(0.9, 0.3);

  public static Item getRandomArmor(EquipmentSlot slot, Random random, double difficulty) {
    var chance = LerpImpl.lerp(ARMOR_SELECT_CHANCE, difficulty);
    var items = getArmorList(slot);
    return getRandomItemBasedOnChance(items, random, chance);
  }
}
