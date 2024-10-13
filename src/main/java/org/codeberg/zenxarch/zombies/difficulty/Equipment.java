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
import org.codeberg.zenxarch.zombies.helper.ItemAttributesImpl;

public abstract class Equipment {
  // private static final Comparator<Double> LOOSE_COMPARE =
  // (a, b) -> Math.abs(b - a) < 0.4 ? 0 : Double.compare(a, b);

  private static double scoreWeapon(Item item) {
    return item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON).ordinal()
            * 5.0
        + ItemAttributesImpl.getZombieAttackDamage(item)
            * ItemAttributesImpl.getZombieAttackSpeed(item);
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

  public static List<Item> getWeaponsList() {
    var tags = List.of(ItemTags.WEAPON_ENCHANTABLE, ItemTags.TRIDENT_ENCHANTABLE);
    return sortStream(toStream(tags), Equipment::scoreWeapon).toList();
  }

  private static List<ArmorItem> getArmorListFromTags(EquipmentSlot slot, List<TagKey<Item>> tags) {
    return sortStream(
            toStream(tags)
                .filter(i -> i instanceof ArmorItem armor && armor.getSlotType().equals(slot))
                .map(i -> (ArmorItem) i),
            v -> scoreArmor(v, slot))
        .toList();
  }

  public static List<ArmorItem> getArmorList(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> getArmorListFromTags(slot, List.of(ItemTags.HEAD_ARMOR_ENCHANTABLE));
      case CHEST -> getArmorListFromTags(slot, List.of(ItemTags.CHEST_ARMOR_ENCHANTABLE));
      case LEGS -> getArmorListFromTags(slot, List.of(ItemTags.LEG_ARMOR_ENCHANTABLE));
      case FEET -> getArmorListFromTags(slot, List.of(ItemTags.FOOT_ARMOR_ENCHANTABLE));
      default -> List.of();
    };
  }
}
