package org.codeberg.zenxarch.zombies.loot_table;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public abstract class EquipmentLootTable {

  private static final List<Item> helmets =
      List.of(
          Items.LEATHER_HELMET,
          Items.CHAINMAIL_HELMET,
          Items.GOLDEN_HELMET,
          Items.IRON_HELMET,
          Items.DIAMOND_HELMET,
          Items.NETHERITE_HELMET);
  private static final List<Item> chestplates =
      List.of(
          Items.LEATHER_CHESTPLATE,
          Items.CHAINMAIL_CHESTPLATE,
          Items.GOLDEN_CHESTPLATE,
          Items.IRON_CHESTPLATE,
          Items.DIAMOND_CHESTPLATE,
          Items.NETHERITE_CHESTPLATE);
  private static final List<Item> leggings =
      List.of(
          Items.LEATHER_LEGGINGS,
          Items.CHAINMAIL_LEGGINGS,
          Items.GOLDEN_LEGGINGS,
          Items.IRON_LEGGINGS,
          Items.DIAMOND_LEGGINGS,
          Items.NETHERITE_LEGGINGS);
  private static final List<Item> boots =
      List.of(
          Items.LEATHER_BOOTS,
          Items.CHAINMAIL_BOOTS,
          Items.GOLDEN_BOOTS,
          Items.IRON_BOOTS,
          Items.DIAMOND_BOOTS,
          Items.NETHERITE_BOOTS);
  private static final List<Item> swords =
      List.of(
          Items.WOODEN_SWORD,
          Items.STONE_SWORD,
          Items.GOLDEN_SWORD,
          Items.IRON_SWORD,
          Items.DIAMOND_SWORD,
          Items.NETHERITE_SWORD);
  private static final List<Item> axes =
      List.of(
          Items.WOODEN_AXE,
          Items.STONE_AXE,
          Items.GOLDEN_AXE,
          Items.IRON_AXE,
          Items.DIAMOND_AXE,
          Items.NETHERITE_AXE);

  private static final List<Integer> weights = List.of(1000, 250, 50, 50, 5, 1, 100);
  private static final List<Integer> qualities = List.of(-1000, -250, 200, 200, 5, 0, 50);

  private static int getWeight(Item item) {
    if (helmets.contains(item)) return weights.get(helmets.indexOf(item));
    if (chestplates.contains(item)) return weights.get(chestplates.indexOf(item));
    if (leggings.contains(item)) return weights.get(leggings.indexOf(item));
    if (boots.contains(item)) return weights.get(boots.indexOf(item));
    if (swords.contains(item)) return weights.get(swords.indexOf(item));
    if (axes.contains(item)) return weights.get(axes.indexOf(item));
    return weights.getLast();
  }

  private static int getQuality(Item item) {
    if (helmets.contains(item)) return qualities.get(helmets.indexOf(item));
    if (chestplates.contains(item)) return qualities.get(chestplates.indexOf(item));
    if (leggings.contains(item)) return qualities.get(leggings.indexOf(item));
    if (boots.contains(item)) return qualities.get(boots.indexOf(item));
    if (swords.contains(item)) return qualities.get(swords.indexOf(item));
    if (axes.contains(item)) return qualities.get(axes.indexOf(item));
    return qualities.getLast();
  }

  public static LootPool.Builder getHelmetPool() {
    return push(getPoolFromList(helmets), Items.TURTLE_HELMET);
  }

  public static LootPool.Builder getChestplatePool() {
    return getPoolFromList(chestplates);
  }

  public static LootPool.Builder getLeggingsPool() {
    return getPoolFromList(leggings);
  }

  public static LootPool.Builder getBootsPool() {
    return getPoolFromList(boots);
  }

  public static LootPool.Builder getSwordsPool() {
    return getPoolFromList(swords);
  }

  public static LootPool.Builder getAxesPool() {
    return getPoolFromList(axes);
  }

  private static LootPool.Builder getPoolFromList(List<Item> items) {
    var result = singleRollLootPool();
    for (var item : items) result = push(result, item);
    return result;
  }

  private static LootPool.Builder push(LootPool.Builder builder, Item item) {
    return builder.with(equipmentEntry(item, getWeight(item), getQuality(item)));
  }

  private static LootPool.Builder singleRollLootPool() {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F));
  }

  private static LeafEntry.Builder<?> equipmentEntry(Item item, int weight, int quality) {
    return ItemEntry.builder(item).weight(weight).quality(quality);
  }
}
