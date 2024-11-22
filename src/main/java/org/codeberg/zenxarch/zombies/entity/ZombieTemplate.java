package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public interface ZombieTemplate {
  void initEquipment(
      ServerWorld world, ZombieEntity zombie, ExtendedDifficulty difficulty, Random random);

  void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked);

  int getWeight();

  boolean canSpawnIn(RegistryEntry<Biome> biome);
}
