package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public interface ZombieTemplate {
  void initEquipment(
      ServerWorld world, ZombieEntity zombie, ExtendedDifficulty difficulty, Random random);

  void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked);
}
