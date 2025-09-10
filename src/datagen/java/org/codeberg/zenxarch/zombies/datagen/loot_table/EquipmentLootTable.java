package org.codeberg.zenxarch.zombies.datagen.loot_table;

import static org.codeberg.zenxarch.zombies.datagen.loot_table.LootTableUtils.*;

import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LeafEntry.Builder;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZEnchantmentProviderGenerator;
import org.codeberg.zenxarch.zombies.loot_table.function.EnchantmentProviderLootFunction;
import org.codeberg.zenxarch.zombies.loot_table.number_provider.LuckLootNumberProvider;

public interface EquipmentLootTable {

  private static LootTable.Builder entry(EquipmentSlot slot, int level) {
    return table(pool(MobEntity.getEquipmentForSlot(slot, level)));
  }

  private static LeafEntry.Builder<?> entry(LootTable.Builder builder) {
    return LootTableEntry.builder(builder.build());
  }

  private static LootPool.Builder build(LootTable.Builder builder) {
    return pool().with(entry(builder).conditionally(RandomChanceLootCondition.builder(0.009f)));
  }

  private static LootTable.Builder getLootTableForLevel(int level) {
    final EquipmentSlot[] slots = {
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    var entries = Stream.of(slots).map(slot -> entry(slot, level)).toList();

    return entries
        .get(0)
        .pool(build(entries.get(1).pool(build(entries.get(2).pool(build(entries.get(3)))))));
  }

  private static LootPoolEntry.Builder<?> applyEnchantment(LootTable.Builder builder, int weight) {
    var enchantment =
        new EnchantmentProviderLootFunction(ZEnchantmentProviderGenerator.ZOMBIE_SPAWN_EQUIPMENT);
    return entry(
            table(
                pool()
                    .with(entry(builder).weight(200 * 1000))
                    .with(entry(builder.apply(enchantment)).weight(0).quality(1000))))
        .weight(weight);
  }

  public static LootPool.Builder axeWeaponTable() {
    return pool()
        .with(ItemEntry.builder(Items.WOODEN_AXE).weight(5))
        .with(ItemEntry.builder(Items.STONE_AXE).weight(100))
        .with(ItemEntry.builder(Items.COPPER_AXE).weight(20))
        .with(ItemEntry.builder(Items.IRON_AXE).weight(10))
        .with(ItemEntry.builder(Items.DIAMOND_AXE).weight(1));
  }

  public static LootPool.Builder leatherOnlyEquipmentTable() {
    return pool()
        .with(applyEnchantment(getLootTableForLevel(0), 1))
        .conditionally(
            RandomChanceLootCondition.builder(LuckLootNumberProvider.create(0.0f, 0.15f)));
  }

  public static LootPool.Builder vanillaEquipmentTable() {
    final int[] equipmentLevel = {0, 1, 2, 3, 4, 5};
    final int[] weights = {33435, 47460, 16505, 2443, 154, 4};
    var pool = pool();
    for (var index : equipmentLevel)
      pool = pool.with(applyEnchantment(getLootTableForLevel(index), weights[index]));
    return pool.conditionally(
        RandomChanceLootCondition.builder(LuckLootNumberProvider.create(0.0f, 0.15f)));
  }

  private static Builder<?> shieldDoorItem(
      Item item, int maxDamage, int defense, float toughness, float knockbackResistance) {

    var customArmor =
        new ArmorMaterial(
            maxDamage,
            Map.of(EquipmentType.CHESTPLATE, defense),
            0,
            null,
            toughness,
            knockbackResistance,
            null,
            null);

    var attributes = customArmor.createAttributeModifiers(EquipmentType.CHESTPLATE);

    var itemPath =
        Registries.ITEM
            .getEntry(item)
            .getKey()
            .map(key -> key.getValue().getPath())
            .orElse("oak_door");
    var setItemModel =
        SetComponentsLootFunction.builder(
            DataComponentTypes.ITEM_MODEL, Identifier.of("zombies_zenxarch", itemPath));

    var setMaxDamage = SetComponentsLootFunction.builder(DataComponentTypes.MAX_DAMAGE, maxDamage);
    var setAttributes =
        SetComponentsLootFunction.builder(DataComponentTypes.ATTRIBUTE_MODIFIERS, attributes);

    return ItemEntry.builder(item).apply(setMaxDamage).apply(setAttributes).apply(setItemModel);
  }

  public static LootPool.Builder shieldDoorEquipment(
      Item item, int maxDamage, int defense, float toughness, float knockbackResistance) {
    return pool().with(shieldDoorItem(item, maxDamage, defense, toughness, knockbackResistance));
  }
}
