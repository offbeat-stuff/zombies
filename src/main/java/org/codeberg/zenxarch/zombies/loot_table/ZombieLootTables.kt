package org.codeberg.zenxarch.zombies.loot_table

import net.minecraft.entity.EquipmentTable
import net.minecraft.loot.LootTable
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import org.codeberg.zenxarch.zombies.Zombies

object ZombieLootTables {
    val COMMON_ZOMBIE_HELMET: RegistryKey<LootTable> = key("common_zombie_equipment_helmet")
    val COMMON_ZOMBIE_CHESTPLATE: RegistryKey<LootTable> = key("common_zombie_equipment_chestplate")
    val COMMON_ZOMBIE_LEGGINGS: RegistryKey<LootTable> = key("common_zombie_equipment_leggings")
    val COMMON_ZOMBIE_BOOTS: RegistryKey<LootTable> = key("common_zombie_equipment_boots")
    val COMMON_ZOMBIE_WEAPONS: RegistryKey<LootTable> = key("common_zombie_equipment_weapons")

    @JvmField
    val COMMON_ZOMBIE_EQUIPMENT: EquipmentTable = equipmentTable("common_zombie_equipment", 0.00075f)

    private fun key(id: String) =
        RegistryKey.of(RegistryKeys.LOOT_TABLE, Zombies.id(id))

    private fun equipmentTable(id: String, slotDropChances: Float) =
        EquipmentTable(key(id), slotDropChances)
}
