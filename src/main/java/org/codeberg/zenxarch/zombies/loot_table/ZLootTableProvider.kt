package org.codeberg.zenxarch.zombies.loot_table

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.condition.RandomChanceLootCondition
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.loot.entry.LootTableEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import org.codeberg.zenxarch.zombies.datagen.ZEnchantmentProviders
import org.codeberg.zenxarch.zombies.loot_table.EquipmentLootTable.axesPool
import org.codeberg.zenxarch.zombies.loot_table.EquipmentLootTable.bootsPool
import org.codeberg.zenxarch.zombies.loot_table.EquipmentLootTable.chestplatePool
import org.codeberg.zenxarch.zombies.loot_table.EquipmentLootTable.helmetPool
import org.codeberg.zenxarch.zombies.loot_table.EquipmentLootTable.leggingsPool
import org.codeberg.zenxarch.zombies.loot_table.EquipmentLootTable.swordsPool
import org.codeberg.zenxarch.zombies.loot_table.LuckLootNumberProvider.Companion.create
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

class ZLootTableProvider(output: FabricDataOutput?, registryLookup: CompletableFuture<WrapperLookup?>?) :
    SimpleFabricLootTableProvider(output, registryLookup, LootContextTypes.EQUIPMENT) {
    override fun accept(registry: BiConsumer<RegistryKey<LootTable>, LootTable.Builder>) {
        val weaponCondition = RandomChanceLootCondition.builder(create())
        val armorCondition = weaponCondition.and(RandomChanceLootCondition.builder(0.5f))
        val enchanter =
            EnchantmentProviderLootFunction(ZEnchantmentProviders.ZOMBIE_SPAWN_EQUIPMENT)

        addLootTable(
            registry,
            ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT.lootTable(),
            singleLootTable(ZombieLootTables.COMMON_ZOMBIE_HELMET)
                .conditionally(armorCondition)
                .apply(enchanter),
            singleLootTable(ZombieLootTables.COMMON_ZOMBIE_CHESTPLATE)
                .conditionally(armorCondition)
                .apply(enchanter),
            singleLootTable(ZombieLootTables.COMMON_ZOMBIE_LEGGINGS)
                .conditionally(armorCondition)
                .apply(enchanter),
            singleLootTable(ZombieLootTables.COMMON_ZOMBIE_BOOTS)
                .conditionally(armorCondition)
                .apply(enchanter),
            singleLootTable(ZombieLootTables.COMMON_ZOMBIE_WEAPONS)
                .conditionally(weaponCondition)
                .apply(enchanter),
            singleRollLootPool(ItemEntry.builder(Items.SHIELD))
                .conditionally(weaponCondition)
                .apply(enchanter)
        )

        val specialWeaponsPool =
            singleRollLootPool(
                ItemEntry.builder(Items.TRIDENT).weight(9), ItemEntry.builder(Items.MACE)
            )

        addLootTable(
            registry,
            ZombieLootTables.COMMON_ZOMBIE_WEAPONS,
            singleRollLootPool(
                lootTableEntry(swordsPool).weight(70),
                lootTableEntry(axesPool).weight(30),
                lootTableEntry(specialWeaponsPool)
            )
        )

        addLootTable(
            registry, ZombieLootTables.COMMON_ZOMBIE_HELMET, helmetPool
        )

        addLootTable(
            registry,
            ZombieLootTables.COMMON_ZOMBIE_CHESTPLATE,
            chestplatePool
        )

        addLootTable(
            registry, ZombieLootTables.COMMON_ZOMBIE_LEGGINGS, leggingsPool
        )

        addLootTable(registry, ZombieLootTables.COMMON_ZOMBIE_BOOTS, bootsPool)
    }

    companion object {
        private fun addLootTable(
            lootTableBiConsumer: BiConsumer<RegistryKey<LootTable>, LootTable.Builder>,
            lootTable: RegistryKey<LootTable>,
            vararg lootPools: LootPool.Builder
        ) {
            var builder = LootTable.builder()
            for (lootPool in lootPools) builder = builder.pool(lootPool)
            lootTableBiConsumer.accept(lootTable, builder)
        }

        private fun lootTableEntry(lootPool: LootPool.Builder): LeafEntry.Builder<*> {
            return LootTableEntry.builder(LootTable.builder().pool(lootPool).build())
        }

        fun singleRollLootPool(vararg entries: LootPoolEntry.Builder<*>?): LootPool.Builder {
            var builder = LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
            for (entry in entries) builder = builder.with(entry)
            return builder
        }

        private fun singleLootTable(table: RegistryKey<LootTable>): LootPool.Builder {
            return singleRollLootPool().with(LootTableEntry.builder(table))
        }
    }
}
