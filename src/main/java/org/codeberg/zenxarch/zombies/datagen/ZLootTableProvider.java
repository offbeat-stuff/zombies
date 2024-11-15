package org.codeberg.zenxarch.zombies.datagen;

import static org.codeberg.zenxarch.zombies.Zombies.id;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.ZombieLootNumberProviderTypes;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public class ZLootTableProvider extends SimpleFabricLootTableProvider {
  public ZLootTableProvider(
      FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
    super(output, registryLookup, LootContextTypes.EQUIPMENT);
  }

  public static final RegistryKey<LootTable> COMMON_ZOMBIE_HELMET =
      key("common_zombie_equipment_helmet");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_CHESTPLATE =
      key("common_zombie_equipment_chestplate");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_LEGGINGS =
      key("common_zombie_equipment_leggings");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_BOOTS =
      key("common_zombie_equipment_boots");

  public static final EquipmentTable COMMON_ZOMBIE_EQUIPMENT =
      equipmentTable("common_zombie_equipment", 0.00075F);

  @Override
  public void accept(BiConsumer<RegistryKey<LootTable>, Builder> lootTableBiConsumer) {
    lootTableBiConsumer.accept(
        COMMON_ZOMBIE_EQUIPMENT.lootTable(),
        LootTable.builder()
            .pool(singleLootTable(COMMON_ZOMBIE_HELMET))
            .pool(singleLootTable(COMMON_ZOMBIE_CHESTPLATE))
            .pool(singleLootTable(COMMON_ZOMBIE_LEGGINGS))
            .pool(singleLootTable(COMMON_ZOMBIE_BOOTS)));

    lootTableBiConsumer.accept(
        COMMON_ZOMBIE_HELMET,
        LootTable.builder()
            .pool(
                singleRollLootPool()
                    .with(equipmentEntry(Items.LEATHER_HELMET, 1000, -25))
                    .with(equipmentEntry(Items.CHAINMAIL_HELMET, 100, 10))
                    .with(equipmentEntry(Items.GOLDEN_HELMET, 0, 0))
                    .with(equipmentEntry(Items.IRON_HELMET, 0, 0))
                    .with(equipmentEntry(Items.DIAMOND_HELMET, 0, 0))
                    .with(equipmentEntry(Items.NETHERITE_HELMET, 0, 0))
                    .with(equipmentEntry(Items.TURTLE_HELMET, 0, 0))));

    lootTableBiConsumer.accept(
        COMMON_ZOMBIE_CHESTPLATE,
        LootTable.builder()
            .pool(
                singleRollLootPool()
                    .with(equipmentEntry(Items.LEATHER_CHESTPLATE, 1000, -25))
                    .with(equipmentEntry(Items.CHAINMAIL_CHESTPLATE, 100, 10))
                    .with(equipmentEntry(Items.GOLDEN_CHESTPLATE, 0, 0))
                    .with(equipmentEntry(Items.IRON_CHESTPLATE, 0, 0))
                    .with(equipmentEntry(Items.DIAMOND_CHESTPLATE, 0, 0))
                    .with(equipmentEntry(Items.NETHERITE_CHESTPLATE, 0, 0))));

    lootTableBiConsumer.accept(
        COMMON_ZOMBIE_LEGGINGS,
        LootTable.builder()
            .pool(
                singleRollLootPool()
                    .with(equipmentEntry(Items.LEATHER_LEGGINGS, 1000, -25))
                    .with(equipmentEntry(Items.CHAINMAIL_LEGGINGS, 100, 10))
                    .with(equipmentEntry(Items.GOLDEN_LEGGINGS, 0, 0))
                    .with(equipmentEntry(Items.IRON_LEGGINGS, 0, 0))
                    .with(equipmentEntry(Items.DIAMOND_LEGGINGS, 0, 0))
                    .with(equipmentEntry(Items.NETHERITE_LEGGINGS, 0, 0))));

    lootTableBiConsumer.accept(
        COMMON_ZOMBIE_BOOTS,
        LootTable.builder()
            .pool(
                singleRollLootPool()
                    .with(equipmentEntry(Items.LEATHER_BOOTS, 1000, -25))
                    .with(equipmentEntry(Items.CHAINMAIL_BOOTS, 100, 10))
                    .with(equipmentEntry(Items.GOLDEN_BOOTS, 0, 0))
                    .with(equipmentEntry(Items.IRON_BOOTS, 0, 0))
                    .with(equipmentEntry(Items.DIAMOND_BOOTS, 0, 0))
                    .with(equipmentEntry(Items.NETHERITE_BOOTS, 0, 0))
                    .conditionally(
                        RandomChanceLootCondition.builder(
                            ZombieLootNumberProviderTypes.LuckLootNumberProvider.create()))));
  }

  private static LootPool.Builder singleRollLootPool() {
    return LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F));
  }

  private static LootPool.Builder singleLootTable(RegistryKey<LootTable> table) {
    return singleRollLootPool().with(LootTableEntry.builder(table));
  }

  private static RegistryKey<LootTable> key(String id) {
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, id(id));
  }

  private static EquipmentTable equipmentTable(String id, float slotDropChances) {
    return new EquipmentTable(key(id), slotDropChances);
  }

  private static LeafEntry.Builder<?> equipmentEntry(Item item, int weight, int quality) {
    return ItemEntry.builder(item).weight(weight).quality(quality);
  }

  public static void addEquipmentTo(
      ServerWorld world,
      MobEntity mob,
      ExtendedDifficulty difficulty,
      EquipmentTable equipmentTable) {
    mob.setEquipmentFromTable(
        equipmentTable.lootTable(),
        new LootWorldContext.Builder(world)
            .add(LootContextParameters.ORIGIN, mob.getPos())
            .add(LootContextParameters.THIS_ENTITY, mob)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT),
        equipmentTable.slotDropChances());
  }
}
