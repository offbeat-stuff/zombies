package org.codeberg.zenxarch.zombies.loot_table;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;

public class ZLootTableProvider extends SimpleFabricLootTableProvider {
  public ZLootTableProvider(
      FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
    super(output, registryLookup, LootContextTypes.EQUIPMENT);
  }

  @Override
  public void accept(BiConsumer<RegistryKey<LootTable>, Builder> lootTableBiConsumer) {
    var condition = RandomChanceLootCondition.builder(LuckLootNumberProvider.create());

    lootTableBiConsumer.accept(
        ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT.lootTable(),
        LootTable.builder()
            .pool(singleLootTable(ZombieLootTables.COMMON_ZOMBIE_HELMET).conditionally(condition))
            .pool(
                singleLootTable(ZombieLootTables.COMMON_ZOMBIE_CHESTPLATE).conditionally(condition))
            .pool(singleLootTable(ZombieLootTables.COMMON_ZOMBIE_LEGGINGS).conditionally(condition))
            .pool(singleLootTable(ZombieLootTables.COMMON_ZOMBIE_BOOTS).conditionally(condition)));

    lootTableBiConsumer.accept(
        ZombieLootTables.COMMON_ZOMBIE_HELMET,
        LootTable.builder().pool(EquipmentLootTable.getHelmetLootPool()));

    lootTableBiConsumer.accept(
        ZombieLootTables.COMMON_ZOMBIE_CHESTPLATE,
        LootTable.builder().pool(EquipmentLootTable.getChestplatePool()));

    lootTableBiConsumer.accept(
        ZombieLootTables.COMMON_ZOMBIE_LEGGINGS,
        LootTable.builder().pool(EquipmentLootTable.getLeggingsPool()));

    lootTableBiConsumer.accept(
        ZombieLootTables.COMMON_ZOMBIE_BOOTS,
        LootTable.builder().pool(EquipmentLootTable.getBootsPool()));
  }

  private static LootPool.Builder singleRollLootPool() {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F));
  }

  private static LootPool.Builder singleLootTable(RegistryKey<LootTable> table) {
    return singleRollLootPool().with(LootTableEntry.builder(table));
  }
}
