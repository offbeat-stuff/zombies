package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.difficulty.Equipment;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public class CommonZombie implements ZombieTemplate {

  @Override
  public ItemStack getEquipmentForSlot(
      ServerWorld world,
      ExtendedDifficulty difficulty,
      ZombieEntity zombie,
      Random random,
      EquipmentSlot slot) {
    var equipment =
        Equipment.getEquipmentForSlot(random, difficulty, slot).map(Item::getDefaultStack);
    if (equipment.isPresent()) zombie.setEquipmentDropChance(slot, 0.00075F);
    return equipment.orElse(ItemStack.EMPTY);
  }

  @Override
  public ItemStack updateEnchantments(
      ServerWorld world,
      ExtendedDifficulty difficulty,
      ZombieEntity zombie,
      Random random,
      EquipmentSlot slot,
      ItemStack stack) {
    return Equipment.enchant(world, random, difficulty, stack);
  }
}
