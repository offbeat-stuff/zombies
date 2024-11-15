package org.codeberg.zenxarch.zombies.loot_table;

import static org.codeberg.zenxarch.zombies.Zombies.id;

import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public abstract class ZombieLootTables {
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

  private static RegistryKey<LootTable> key(String id) {
    return RegistryKey.of(RegistryKeys.LOOT_TABLE, id(id));
  }

  private static EquipmentTable equipmentTable(String id, float slotDropChances) {
    return new EquipmentTable(key(id), slotDropChances);
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
