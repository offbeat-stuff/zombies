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
    addLootTable(
        registry,
        ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT.lootTable(),
        EquipmentLootTable.vanillaEquipmentTable(),
        LootPool.builder()
            .with(ItemEntry.builder(Items.IRON_SWORD))
            .with(ItemEntry.builder(Items.IRON_SHOVEL).weight(2))
            .conditionally(RandomChanceLootCondition.builder(0.05f)));
  }
}
