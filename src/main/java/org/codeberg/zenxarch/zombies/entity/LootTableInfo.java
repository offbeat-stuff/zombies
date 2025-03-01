package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public record LootTableInfo(EquipmentTable table, RegistryKey<LootTable> onDrop) {

  public static final LootTableInfo DEFAULT =
      new LootTableInfo(
          ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT,
          RegistryKey.of(
              RegistryKeys.LOOT_TABLE,
              EntityType.getId(EntityType.ZOMBIE).withPrefixedPath("entities/")));

  public static final Codec<LootTableInfo> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      EquipmentTable.CODEC
                          .optionalFieldOf("table", ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT)
                          .forGetter(LootTableInfo::table),
                      RegistryKey.createCodec(RegistryKeys.LOOT_TABLE)
                          .optionalFieldOf("onDrop", DEFAULT.onDrop)
                          .forGetter(LootTableInfo::onDrop))
                  .apply(instance, LootTableInfo::new));

  public void initEquipment(
      ServerWorld world, ExtendedZombieEntity zombie, ExtendedDifficulty difficulty) {
    zombie.setEquipmentFromTable(
        table.lootTable(),
        new LootContextParameterSet.Builder(world)
            .add(LootContextParameters.ORIGIN, zombie.getPos())
            .add(LootContextParameters.THIS_ENTITY, zombie)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT),
        table.slotDropChances());
  }
}
