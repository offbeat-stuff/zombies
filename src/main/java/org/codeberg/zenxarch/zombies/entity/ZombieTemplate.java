package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;

public interface ZombieTemplate {
  void initEquipment(
      ServerWorld world, ZombieEntity zombie, ExtendedDifficulty difficulty, Random random);

  ZombieEffect onAttackEffect();

  ZombieEffect onKillEffect();

  ZombieEffect onTickEffect();

  int getWeight();

  boolean canSpawnIn(RegistryEntry<Biome> biome);
}
