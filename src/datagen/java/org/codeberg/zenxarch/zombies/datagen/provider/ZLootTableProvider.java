package org.codeberg.zenxarch.zombies.datagen.provider;

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
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.datagen.loot_table.EquipmentLootTable;

public class ZLootTableProvider extends SimpleFabricLootTableProvider {

  public static final EquipmentTable COMMON_ZOMBIE_EQUIPMENT =
      equipmentTable("common_zombie_equipment", 0.00075F);

  public static final EquipmentTable AXE_ZOMBIE_EQUIPMENT =
      equipmentTable("axe_zombie_equipment", 0.00075F);

  public static final EquipmentTable OAK_DOOR_SHIELD_EQUIPMENT =
      equipmentTable("oak_door_shield_equipment", 0F);

  public static final EquipmentTable COPPER_DOOR_SHIELD_EQUIPMENT =
      equipmentTable("copper_door_shield_equipment", 0F);

  public static final EquipmentTable IRON_DOOR_SHIELD_EQUIPMENT =
      equipmentTable("iron_door_shield_equipment", 0F);

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
    addLootTable(
        registry,
        COMMON_ZOMBIE_EQUIPMENT.lootTable(),
        EquipmentLootTable.vanillaEquipmentTable(),
        LootPool.builder()
            .with(ItemEntry.builder(Items.IRON_SWORD))
            .with(ItemEntry.builder(Items.IRON_SHOVEL).weight(2))
            .conditionally(RandomChanceLootCondition.builder(0.05f)));
    addLootTable(
        registry,
        AXE_ZOMBIE_EQUIPMENT.lootTable(),
        EquipmentLootTable.leatherOnlyEquipmentTable(),
        EquipmentLootTable.axeWeaponTable());

    addLootTable(
        registry,
        OAK_DOOR_SHIELD_EQUIPMENT.lootTable(),
        EquipmentLootTable.shieldDoorEquipment(Items.OAK_DOOR, 125, 12, 5.0f, 0.3f));
    addLootTable(
        registry,
        COPPER_DOOR_SHIELD_EQUIPMENT.lootTable(),
        EquipmentLootTable.shieldDoorEquipment(Items.COPPER_DOOR, 250, 16, 6.0f, 0.4f));
    addLootTable(
        registry,
        IRON_DOOR_SHIELD_EQUIPMENT.lootTable(),
        EquipmentLootTable.shieldDoorEquipment(Items.IRON_DOOR, 375, 20, 7.0f, 0.5f));
  }
}
