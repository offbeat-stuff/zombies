package org.codeberg.zenxarch.default_zombies.datagen.provider;

import static org.codeberg.zenxarch.default_zombies.datagen.loot_table.LootTableUtils.singleResultPool;
import static org.codeberg.zenxarch.default_zombies.datagen.loot_table.LootTableUtils.tableEntry;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import org.codeberg.zenxarch.default_zombies.datagen.loot_table.EquipmentLootTable;
import org.codeberg.zenxarch.zombies.Zombies;

public class ZLootTableProvider extends SimpleFabricLootTableProvider {

  private static EquipmentTable lowDrop(String id) {
    return equipmentTable(id, 0.00075F);
  }

  private static EquipmentTable noDrop(String id) {
    return equipmentTable(id, 0);
  }

  public static final RegistryKey<LootTable> LEATHER_EQUIPMENT = key("leather_equipment");
  public static final RegistryKey<LootTable> COPPER_EQUIPMENT = key("copper_equipment");
  public static final RegistryKey<LootTable> GOLDEN_EQUIPMENT = key("golden_equipment");
  public static final RegistryKey<LootTable> CHAINMAIL_EQUIPMENT = key("chainmail_equipment");
  public static final RegistryKey<LootTable> IRON_EQUIPMENT = key("iron_equipment");
  public static final RegistryKey<LootTable> DIAMOND_EQUIPMENT = key("diamond_equipment");

  public static final RegistryKey<LootTable> ENCHANTED_LEATHER_EQUIPMENT =
      key("enchanted_leather_equipment");
  public static final RegistryKey<LootTable> ENCHANTED_COPPER_EQUIPMENT =
      key("enchanted_copper_equipment");
  public static final RegistryKey<LootTable> ENCHANTED_GOLDEN_EQUIPMENT =
      key("enchanted_golden_equipment");
  public static final RegistryKey<LootTable> ENCHANTED_CHAINMAIL_EQUIPMENT =
      key("enchanted_chainmail_equipment");
  public static final RegistryKey<LootTable> ENCHANTED_IRON_EQUIPMENT =
      key("enchanted_iron_equipment");
  public static final RegistryKey<LootTable> ENCHANTED_DIAMOND_EQUIPMENT =
      key("enchanted_diamond_equipment");

  public static final EquipmentTable COMMON_ZOMBIE_EQUIPMENT = lowDrop("common_zombie_equipment");

  public static final EquipmentTable AXE_ZOMBIE_EQUIPMENT = lowDrop("axe_zombie_equipment");

  public static final EquipmentTable OAK_DOOR_SHIELD_EQUIPMENT =
      noDrop("oak_door_shield_equipment");

  public static final EquipmentTable COPPER_DOOR_SHIELD_EQUIPMENT =
      noDrop("copper_door_shield_equipment");

  public static final EquipmentTable IRON_DOOR_SHIELD_EQUIPMENT =
      noDrop("iron_door_shield_equipment");

  private static EquipmentTable equipmentTable(String id, float slotDropChances) {
    return new EquipmentTable(key(id), slotDropChances);
  }

  private static RegistryKey<LootTable> key(String id) {
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, Zombies.id(id));
  }

  public ZLootTableProvider(
      FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
    super(output, registryLookup, LootContextTypes.EQUIPMENT);
  }

  private static void addLootTable(
      BiConsumer<RegistryKey<LootTable>, Builder> lootTableBiConsumer,
      RegistryKey<LootTable> lootTable,
      LootPool.Builder... lootPools) {
    var builder = LootTable.builder();
    for (var lootPool : lootPools) builder = builder.pool(lootPool);
    lootTableBiConsumer.accept(lootTable, builder);
  }

  @Override
  public void accept(BiConsumer<RegistryKey<LootTable>, Builder> registry) {

    {
      registry.accept(LEATHER_EQUIPMENT, EquipmentLootTable.getLootTableForLevel(0));
      registry.accept(COPPER_EQUIPMENT, EquipmentLootTable.getLootTableForLevel(1));
      registry.accept(GOLDEN_EQUIPMENT, EquipmentLootTable.getLootTableForLevel(2));
      registry.accept(CHAINMAIL_EQUIPMENT, EquipmentLootTable.getLootTableForLevel(3));
      registry.accept(IRON_EQUIPMENT, EquipmentLootTable.getLootTableForLevel(4));
      registry.accept(DIAMOND_EQUIPMENT, EquipmentLootTable.getLootTableForLevel(5));
    }
    {
      registry.accept(
          ENCHANTED_LEATHER_EQUIPMENT, EquipmentLootTable.applyEnchantment(LEATHER_EQUIPMENT));
      registry.accept(
          ENCHANTED_COPPER_EQUIPMENT, EquipmentLootTable.applyEnchantment(COPPER_EQUIPMENT));
      registry.accept(
          ENCHANTED_GOLDEN_EQUIPMENT, EquipmentLootTable.applyEnchantment(GOLDEN_EQUIPMENT));
      registry.accept(
          ENCHANTED_CHAINMAIL_EQUIPMENT, EquipmentLootTable.applyEnchantment(CHAINMAIL_EQUIPMENT));
      registry.accept(
          ENCHANTED_IRON_EQUIPMENT, EquipmentLootTable.applyEnchantment(IRON_EQUIPMENT));
      registry.accept(
          ENCHANTED_DIAMOND_EQUIPMENT, EquipmentLootTable.applyEnchantment(DIAMOND_EQUIPMENT));
    }
    {
      addLootTable(
          registry,
          COMMON_ZOMBIE_EQUIPMENT.lootTable(),
          EquipmentLootTable.withVanillaArmorSpawnChance(
              singleResultPool(
                  EquipmentLootTable.getLootPoolForEquipmentLevel(ENCHANTED_LEATHER_EQUIPMENT, 0),
                  EquipmentLootTable.getLootPoolForEquipmentLevel(ENCHANTED_COPPER_EQUIPMENT, 1),
                  EquipmentLootTable.getLootPoolForEquipmentLevel(ENCHANTED_GOLDEN_EQUIPMENT, 2),
                  EquipmentLootTable.getLootPoolForEquipmentLevel(ENCHANTED_CHAINMAIL_EQUIPMENT, 3),
                  EquipmentLootTable.getLootPoolForEquipmentLevel(ENCHANTED_IRON_EQUIPMENT, 4),
                  EquipmentLootTable.getLootPoolForEquipmentLevel(ENCHANTED_DIAMOND_EQUIPMENT, 5))),
          singleResultPool(
                  ItemEntry.builder(Items.IRON_SWORD),
                  ItemEntry.builder(Items.IRON_SHOVEL).weight(2))
              .conditionally(RandomChanceLootCondition.builder(0.05f)));
      addLootTable(
          registry,
          AXE_ZOMBIE_EQUIPMENT.lootTable(),
          EquipmentLootTable.withVanillaArmorSpawnChance(
              singleResultPool(tableEntry(ENCHANTED_LEATHER_EQUIPMENT))),
          EquipmentLootTable.axeWeaponTable());
    }

    {
      final var durability = 125;
      addLootTable(
          registry,
          OAK_DOOR_SHIELD_EQUIPMENT.lootTable(),
          EquipmentLootTable.shieldDoorEquipment(Items.OAK_DOOR, durability, 12, 5.0f, 0.3f));
      addLootTable(
          registry,
          COPPER_DOOR_SHIELD_EQUIPMENT.lootTable(),
          EquipmentLootTable.shieldDoorEquipment(
              Items.COPPER_DOOR, durability * 2, 16, 6.0f, 0.4f));
      addLootTable(
          registry,
          IRON_DOOR_SHIELD_EQUIPMENT.lootTable(),
          EquipmentLootTable.shieldDoorEquipment(Items.IRON_DOOR, durability * 3, 20, 7.0f, 0.5f));
    }
  }
}
