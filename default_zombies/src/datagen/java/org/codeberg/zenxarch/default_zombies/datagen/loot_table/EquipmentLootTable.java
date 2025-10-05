package org.codeberg.zenxarch.default_zombies.datagen.loot_table;

import static org.codeberg.zenxarch.default_zombies.datagen.loot_table.LootTableUtils.*;

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
import net.minecraft.loot.entry.LeafEntry.Builder;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.default_zombies.datagen.dynamic.ZEnchantmentProviderGenerator;
import org.codeberg.zenxarch.zombies.loot_table.function.EnchantmentProviderLootFunction;
import org.codeberg.zenxarch.zombies.loot_table.number_provider.LuckLootNumberProvider;

public interface EquipmentLootTable {

  private static LootTable.Builder entryForLevel(EquipmentSlot slot, int level) {
    return table(pool(MobEntity.getEquipmentForSlot(slot, level)));
  }

  private static LootPool.Builder applyKeepEntryCondition(LootTable.Builder builder) {
    var keepEntryCondition = RandomChanceLootCondition.builder(0.9F);

    return pool().with(tableEntry(builder).conditionally(keepEntryCondition));
  }

  public static LootTable.Builder getLootTableForLevel(int level) {
    final EquipmentSlot[] slots = {
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    var entries = Stream.of(slots).map(slot -> entryForLevel(slot, level)).toList();

    return entries
        .get(0)
        .pool(
            applyKeepEntryCondition(
                entries
                    .get(1)
                    .pool(
                        applyKeepEntryCondition(
                            entries.get(2).pool(applyKeepEntryCondition(entries.get(3)))))));
  }

  public static LootTable.Builder applyEnchantment(RegistryKey<LootTable> table) {
    var enchantment =
        new EnchantmentProviderLootFunction(ZEnchantmentProviderGenerator.ZOMBIE_SPAWN_EQUIPMENT);
    /*
     * Matches MobEntity.enchantEquipment for power 0.5 on 1.21.9
     * w1 = ?
     * ql1 = -w1 / 2
     * w2 = 0
     * ql2 = w1 / 2
     */
    final var weightUnenchanted = 1000;
    return table(
        pool()
            .with(tableEntry(table).weight(weightUnenchanted).quality(-weightUnenchanted / 2))
            .with(
                (tableEntry(table).apply(() -> enchantment))
                    .weight(0)
                    .quality(weightUnenchanted / 2)));
  }

  public static LootPool.Builder axeWeaponTable() {
    return pool()
        .with(ItemEntry.builder(Items.WOODEN_AXE).weight(5))
        .with(ItemEntry.builder(Items.STONE_AXE).weight(100))
        .with(ItemEntry.builder(Items.COPPER_AXE).weight(20))
        .with(ItemEntry.builder(Items.IRON_AXE).weight(10))
        .with(ItemEntry.builder(Items.DIAMOND_AXE).weight(1));
  }

  public static LootPool.Builder withVanillaArmorSpawnChance(LootPool.Builder builder) {
    return builder.conditionally(
        RandomChanceLootCondition.builder(LuckLootNumberProvider.create(.0f, 0.15f)));
  }

  public static LootPoolEntry.Builder<?> getLootPoolForEquipmentLevel(
      RegistryKey<LootTable> table, int level) {
    final int[] weights = {23599, 32192, 33350, 9677, 1149, 34};

    return tableEntry(table).weight(weights[level]);
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
