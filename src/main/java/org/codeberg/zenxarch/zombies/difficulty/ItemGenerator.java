package org.codeberg.zenxarch.zombies.difficulty;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.helper.ItemAttributesImpl;
import org.codeberg.zenxarch.zombies.helper.ItemSelector;
import org.codeberg.zenxarch.zombies.registry.TagEntry;
import org.codeberg.zenxarch.zombies.registry.TagEntryListBuilder;
import org.codeberg.zenxarch.zombies.registry.TagEntryListBuilder.InnerTagEntryListBuilder;

public record ItemGenerator(List<TagEntry> entries, EquipmentSlot slot, ItemSelector selector) {

  public static ItemGenerator fromBuilder(
      Consumer<TagEntryListBuilder> buildFunction,
      Consumer<InnerTagEntryListBuilder<Item>> valueFunction,
      EquipmentSlot slot,
      ItemSelector selector) {
    var builder = new TagEntryListBuilder();
    buildFunction.accept(builder);
    builder.add(Registries.ITEM, valueFunction);
    return new ItemGenerator(builder.build(), slot, selector);
  }

  public static ItemGenerator fromTag(TagKey<Item> tag, EquipmentSlot slot, ItemSelector selector) {
    return fromBuilder(b -> b.add(tag), v -> {}, slot, selector);
  }

  public static ItemGenerator fromItem(Item item, EquipmentSlot slot, ItemSelector selector) {
    return fromBuilder(b -> {}, v -> v.add(item), slot, selector);
  }

  public Item generate(Random random, double difficulty) {
    var items =
        this.entries.stream()
            .flatMap(v -> v.streamValues(Registries.ITEM))
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
}
