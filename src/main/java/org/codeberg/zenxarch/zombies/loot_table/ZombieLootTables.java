package org.codeberg.zenxarch.zombies.loot_table;

import static org.codeberg.zenxarch.zombies.Zombies.id;

import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public abstract class ZombieLootTables {
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_HELMET =
      key("common_zombie_equipment_helmet");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_CHESTPLATE =
      key("common_zombie_equipment_chestplate");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_LEGGINGS =
      key("common_zombie_equipment_leggings");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_BOOTS =
      key("common_zombie_equipment_boots");
  public static final RegistryKey<LootTable> COMMON_ZOMBIE_WEAPONS =
      key("common_zombie_equipment_weapons");

  public static final EquipmentTable COMMON_ZOMBIE_EQUIPMENT =
      equipmentTable("common_zombie_equipment", 0.00075F);

  private static RegistryKey<LootTable> key(String id) {
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, id(id));
  }

  private static EquipmentTable equipmentTable(String id, float slotDropChances) {
    return new EquipmentTable(key(id), slotDropChances);
  }
}
