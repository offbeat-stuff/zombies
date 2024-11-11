package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public interface ZombieTemplate {
  ItemStack getEquipmentForSlot(
      ServerWorld world,
      ExtendedDifficulty difficulty,
      ZombieEntity zombie,
      Random random,
      EquipmentSlot slot);

  ItemStack updateEnchantments(
      ServerWorld world,
      ExtendedDifficulty difficulty,
      ZombieEntity zombie,
      Random random,
      EquipmentSlot slot,
      ItemStack stack);
}
