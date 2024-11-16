package org.codeberg.zenxarch.zombies.loot_table;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public class ZLootTableProvider extends SimpleFabricLootTableProvider {
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
    var weaponCondition = RandomChanceLootCondition.builder(LuckLootNumberProvider.create());
    var armorCondition = weaponCondition.and(RandomChanceLootCondition.builder(0.5F));

    addLootTable(
        registry,
        ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT.lootTable(),
        singleLootTable(ZombieLootTables.COMMON_ZOMBIE_HELMET).conditionally(armorCondition),
        singleLootTable(ZombieLootTables.COMMON_ZOMBIE_CHESTPLATE).conditionally(armorCondition),
        singleLootTable(ZombieLootTables.COMMON_ZOMBIE_LEGGINGS).conditionally(armorCondition),
        singleLootTable(ZombieLootTables.COMMON_ZOMBIE_BOOTS).conditionally(armorCondition),
        singleLootTable(ZombieLootTables.COMMON_ZOMBIE_WEAPONS).conditionally(weaponCondition),
        singleRollLootPool(ItemEntry.builder(Items.SHIELD)).conditionally(weaponCondition));

    var specialWeaponsPool =
        singleRollLootPool(
            ItemEntry.builder(Items.TRIDENT).weight(9), ItemEntry.builder(Items.MACE));

    addLootTable(
        registry,
        ZombieLootTables.COMMON_ZOMBIE_WEAPONS,
        singleRollLootPool(
            lootTableEntry(EquipmentLootTable.getSwordsPool()).weight(70),
            lootTableEntry(EquipmentLootTable.getAxesPool()).weight(30),
            lootTableEntry(specialWeaponsPool)));

    addLootTable(
        registry, ZombieLootTables.COMMON_ZOMBIE_HELMET, EquipmentLootTable.getHelmetPool());

    addLootTable(
        registry,
        ZombieLootTables.COMMON_ZOMBIE_CHESTPLATE,
        EquipmentLootTable.getChestplatePool());

    addLootTable(
        registry, ZombieLootTables.COMMON_ZOMBIE_LEGGINGS, EquipmentLootTable.getLeggingsPool());

    addLootTable(registry, ZombieLootTables.COMMON_ZOMBIE_BOOTS, EquipmentLootTable.getBootsPool());
  }

  private static LeafEntry.Builder<?> lootTableEntry(LootPool.Builder lootPool) {
    return LootTableEntry.builder(LootTable.builder().pool(lootPool).build());
  }

  public static LootPool.Builder singleRollLootPool(LootPoolEntry.Builder<?>... entries) {
    var builder = LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F));
    for (var entry : entries) builder = builder.with(entry);
    return builder;
  }

  private static LootPool.Builder singleLootTable(RegistryKey<LootTable> table) {
    return singleRollLootPool().with(LootTableEntry.builder(table));
  }
}
