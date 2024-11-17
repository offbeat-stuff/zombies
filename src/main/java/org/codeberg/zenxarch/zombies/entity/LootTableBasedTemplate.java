package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public record LootTableBasedTemplate(EquipmentTable equipmentTable) implements ZombieTemplate {

  @Override
  public void initEquipment(
      ServerWorld world, ZombieEntity zombie, ExtendedDifficulty difficulty, Random random) {
    zombie.setEquipmentFromTable(
        equipmentTable.lootTable(),
        new LootWorldContext.Builder(world)
            .add(LootContextParameters.ORIGIN, zombie.getPos())
            .add(LootContextParameters.THIS_ENTITY, zombie)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT),
        equipmentTable.slotDropChances());
  }
}
