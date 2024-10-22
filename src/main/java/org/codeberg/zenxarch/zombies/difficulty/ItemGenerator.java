package org.codeberg.zenxarch.zombies.difficulty;

import com.mojang.datafixers.util.Either;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.helper.ItemAttributesImpl;
import org.codeberg.zenxarch.zombies.helper.ItemSelector;
import org.codeberg.zenxarch.zombies.helper.OptionalPair;

public record ItemGenerator(
    List<OptionalPair<TagKey<Item>, Item>> entries, EquipmentSlot slot, ItemSelector selector) {

  public static ItemGenerator items(List<Item> items, EquipmentSlot slot, ItemSelector selector) {
    return new ItemGenerator(
        items.stream().map(OptionalPair::<TagKey<Item>, Item>right).toList(), slot, selector);
  }

  public static ItemGenerator item(Item item, EquipmentSlot slot, ItemSelector selector) {
    return items(List.of(item), slot, selector);
  }

  public static ItemGenerator tags(
      List<TagKey<Item>> tags, EquipmentSlot slot, ItemSelector selector) {
    return new ItemGenerator(
        tags.stream().map(OptionalPair::<TagKey<Item>, Item>left).toList(), slot, selector);
  }

  public static ItemGenerator tag(TagKey<Item> tag, EquipmentSlot slot, ItemSelector selector) {
    return tags(List.of(tag), slot, selector);
  }

  public Item generate(Random random, double difficulty) {
    var registry = Registries.ITEM;
    Function<OptionalPair<TagKey<Item>, Item>, Stream<Item>> optToItem =
        v ->
            v.mapAndUnwrap(
                    tag ->
                        registry.getOptional(tag).stream()
                            .flatMap(Named::stream)
                            .map(
                                entry ->
                                    Either.unwrap(entry.getKeyOrValue().mapLeft(registry::get))),
                    Stream::of)
                .orElse(Stream.empty());
    var items =
        this.entries.stream()
            .flatMap(optToItem)
            .filter(item -> matchesSlot(item, this.slot))
            .sorted(Comparator.comparingDouble(ItemGenerator::score))
            .distinct()
            .toList();
    var index = this.selector.apply(items, random, difficulty);
    if (index == -1) return null;
    return items.get(index);
  }

  private static Rarity getRarity(Item item) {
    return item.getComponents().getOrDefault(DataComponentTypes.RARITY, Rarity.COMMON);
  }

  private static double scoreWeapon(Item item) {
    var damageBonus = ItemAttributesImpl.getZombieAttackDamage(item);
    var rarityBonus = getRarity(item).ordinal() * 5.0;
    var enchantableComponent = item.getComponents().get(DataComponentTypes.ENCHANTABLE);
    var enchantabilityBonus =
        (enchantableComponent == null ? 0 : enchantableComponent.value()) / 15.0;
    return damageBonus + rarityBonus + enchantabilityBonus;
  }

  private static double scoreArmor(ArmorItem armor) {
    var equippableComponent = armor.getComponents().get(DataComponentTypes.EQUIPPABLE);
    var slot = equippableComponent == null ? EquipmentSlot.MAINHAND : equippableComponent.slot();
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
    if (item.getComponents().contains(DataComponentTypes.EQUIPPABLE))
      return item.getComponents().get(DataComponentTypes.EQUIPPABLE).slot().equals(slot);
    return slot.getType().equals(EquipmentSlot.Type.HAND);
  }
}
