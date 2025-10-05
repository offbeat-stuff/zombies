package org.codeberg.zenxarch.default_zombies.datagen.provider;

import static net.minecraft.loot.entry.ItemEntry.builder;
import static net.minecraft.loot.entry.LootTableEntry.builder;
import static org.codeberg.zenxarch.default_zombies.datagen.loot_table.LootTableUtils.*;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootTableProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import org.codeberg.zenxarch.zombies.Zombies;
import org.jetbrains.annotations.NotNull;

public class ZEntityLootTableProvider extends FabricEntityLootTableProvider {

  public ZEntityLootTableProvider(
      FabricDataOutput output, @NotNull CompletableFuture<WrapperLookup> registryLookup) {
    super(output, registryLookup);
  }

  private static RegistryKey<LootTable> key(String id) {
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, Zombies.id(id));
  }

  public static final RegistryKey<LootTable> ZOMBIE_DROPS = key("zombie_drops");

  private void addLootTable(
      EntityType<?> type, RegistryKey<LootTable> key, LootPool.Builder... lootPools) {
    this.register(type, key, table(lootPools));
  }

  @Override
  public void generate() {
    var boneEntry =
        builder(Items.BONE)
            .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0.0F, 2.0F)))
            .apply(
                EnchantedCountIncreaseLootFunction.builder(
                    this.registries, UniformLootNumberProvider.create(0.0F, 1.0F)));
    addLootTable(
        EntityType.ZOMBIE,
        ZOMBIE_DROPS,
        singleResultPool().with(builder(EntityType.ZOMBIE.getLootTableKey().orElseThrow())),
        singleResultPool().with(boneEntry));
  }
}
