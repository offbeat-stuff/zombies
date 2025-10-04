package org.codeberg.zenxarch.default_zombies.datagen.loot_table;

import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public final class LootTableUtils {
  private LootTableUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static LootPool.Builder pool() {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f));
  }

  public static LootPool.Builder pool(Item item) {
    return pool().with(ItemEntry.builder(item));
  }

  public static LootTable.Builder table(LootPool.Builder... pools) {
    var result = LootTable.builder();
    for (var pool : pools) result = result.pool(pool);
    return result;
  }

  public static LeafEntry.Builder<?> tableEntry(LootTable.Builder builder) {
    return LootTableEntry.builder(builder.build());
  }
}
