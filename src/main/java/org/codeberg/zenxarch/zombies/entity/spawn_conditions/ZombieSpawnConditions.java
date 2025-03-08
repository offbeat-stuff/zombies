package org.codeberg.zenxarch.zombies.entity.spawn_conditions;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.codeberg.zenxarch.zombies.Zombies;

public final class ZombieSpawnConditions {
  private ZombieSpawnConditions() {
    throw new IllegalStateException("Utility class");
  }

  public static void initialize() {
    Registry.register(
        Registries.SPAWN_CONDITION_TYPE, Zombies.id("is_day"), DaySpawnCondition.CODEC);
    Registry.register(
        Registries.SPAWN_CONDITION_TYPE, Zombies.id("is_night"), NightSpawnCondition.CODEC);
    Registry.register(
        Registries.SPAWN_CONDITION_TYPE, Zombies.id("negate"), NegateSpawnCondition.CODEC);
    Registry.register(
        Registries.SPAWN_CONDITION_TYPE, Zombies.id("all_of"), AllOfSpawnCondition.CODEC);
  }
}
