package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
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

  private static double scoreArmor(ArmorItem item, EquipmentSlot slot) {
    return ItemAttributesImpl.getZombieArmor(item, slot)
        + ItemAttributesImpl.getZombieArmorToughness(item, slot)
        + ItemAttributesImpl.getZombieKnockbackResistance(item, slot);
  }

  private static <T> Stream<T> reduceStream(Stream<List<T>> streams) {
    return streams.map(List::stream).reduce(Stream.empty(), Stream::concat);
  }

  private static List<Item> getListFrom(TagKey<Item> tag) {
    var registry = Registries.ITEM;
    var entries = registry.getOrCreateEntryList(tag);
    return entries.stream().map(v -> v.getKeyOrValue().map(a -> registry.get(a), b -> b)).toList();
  }

  private static Stream<Item> toStream(List<TagKey<Item>> tags) {
    return reduceStream(tags.stream().map(Equipment::getListFrom));
  }

  private static <T> Stream<T> sortStream(Stream<T> stream, Function<T, Double> score) {
    return stream.sorted((a, b) -> Double.compare(score.apply(a), score.apply(b)));
  }

  private static List<Item> getWeaponsList(List<TagKey<Item>> tags) {
    return sortStream(toStream(tags), Equipment::scoreWeapon).toList();
  }

  private static <T> T getRandomItemBasedOnChance(List<T> list, Random random, double chance) {
    if (list.isEmpty()) return null;
    for (var v : list) if (ProbabilityImpl.nextBoolean(random, chance)) return v;
    return list.get(0);
  }

  public static Item getRandomWeapon(Random random, double difficulty) {
    var axes = getWeaponsList(List.of(ItemTags.AXES));
    var swords = getWeaponsList(List.of(ItemTags.SWORD_ENCHANTABLE));
    var special = getWeaponsList(List.of(ItemTags.TRIDENT_ENCHANTABLE, ItemTags.MACE_ENCHANTABLE));
    var selected = LerpImpl.lerpWeighted(random, 1.0, 250.0, 3);
    if (selected == 0) return getRandomItemBasedOnChance(special, random, 0.3);
    return LerpImpl.lerpWeighted(
        random,
        LerpImpl.lerp(List.of(1000.0, 100.0), difficulty),
        1.0,
        selected == 1 ? axes : swords);
  }

  private static List<ArmorItem> getArmorListFromTags(EquipmentSlot slot, List<TagKey<Item>> tags) {
    return sortStream(
            toStream(tags)
                .filter(i -> i instanceof ArmorItem armor && armor.getSlotType().equals(slot))
                .map(i -> (ArmorItem) i),
            v -> scoreArmor(v, slot))
        .toList();
  }

  private static List<ArmorItem> getArmorList(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> getArmorListFromTags(slot, List.of(ItemTags.HEAD_ARMOR_ENCHANTABLE));
      case CHEST -> getArmorListFromTags(slot, List.of(ItemTags.CHEST_ARMOR_ENCHANTABLE));
      case LEGS -> getArmorListFromTags(slot, List.of(ItemTags.LEG_ARMOR_ENCHANTABLE));
      case FEET -> getArmorListFromTags(slot, List.of(ItemTags.FOOT_ARMOR_ENCHANTABLE));
      default -> List.of();
    };
  }

  private static final List<Double> ARMOR_SELECT_CHANCE = List.of(0.9, 0.3);

  public static ArmorItem getRandomArmor(EquipmentSlot slot, Random random, double difficulty) {
    var chance = LerpImpl.lerp(ARMOR_SELECT_CHANCE, difficulty);
    var items = getArmorList(slot);
    return getRandomItemBasedOnChance(items, random, chance);
  }
}
