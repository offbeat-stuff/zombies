package org.codeberg.zenxarch.default_zombies.datagen.loot_table;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;

public final class LootTableUtils {
  private LootTableUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static LootPool.Builder singleResultPool() {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f));
  }

  public static LootPool.Builder singleResultPool(LootPoolEntry.Builder<?>... entries) {
    var result = singleResultPool();
    for (var entry : entries) result.with(entry);
    return result;
  }

  public static LootPool.Builder singleItemPool(Item item) {
    return singleResultPool().with(ItemEntry.builder(item));
  }

  public static LootTable.Builder table(LootPool.Builder... pools) {
    var result = LootTable.builder();
    for (var pool : pools) result.pool(pool);
    return result;
  }

  public static LootTableEntry.Builder<?> tableEntry(LootTable.Builder builder) {
    return LootTableEntry.builder(builder.build());
  }

  public static LootTableEntry.Builder<?> tableEntry(RegistryKey<LootTable> table) {
    return LootTableEntry.builder(table);
  }
}
