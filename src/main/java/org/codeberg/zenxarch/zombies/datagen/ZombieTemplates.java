package org.codeberg.zenxarch.zombies.datagen;

import static org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect.*;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;
import org.codeberg.zenxarch.zombies.entity.ZombieTemplate;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;

public interface ZombieTemplates {
  public static RegistryKey<ZombieTemplate> of(String path) {
    return RegistryKey.of(ZombieRegistries.TEMPLATE_REGISTRY_KEY, Zombies.id(path));
  }

  private static ZombieTemplate.Builder defaultBuilder() {
    return ZombieTemplate.builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT);
  }

  private static ZombieTemplate.Builder defaultBuilder(int weight) {
    return ZombieTemplate.builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT).withWeight(weight);
  }

  public static final String COMMON_ZOMBIE = "default";
  public static final String SWAPPING_ZOMBIE = "swapping";
  public static final String FIRE_ZOMBIE = "fire";
  public static final String FREEZE_ZOMBIE = "freeze";

  static void bootstrap(Registerable<ZombieTemplate> registry) {
    register(registry, COMMON_ZOMBIE, defaultBuilder(512));
    register(
        registry,
        SWAPPING_ZOMBIE,
        defaultBuilder()
            .withOnAttack(swapPositions())
            .withOnTick(spawnParticles(ParticleTypes.PORTAL, 0.2F)));
    register(
        registry,
        FIRE_ZOMBIE,
        defaultBuilder(16)
            .withOnAttack(ignite(1.0F))
            .withOnTick(spawnParticles(ParticleTypes.FLAME, 0.2F))
            .spawnIn(ZBiomeTags.WITH_FLAME_ZOMBIES));
    register(
        registry,
        FREEZE_ZOMBIE,
        defaultBuilder(16)
            .withOnAttack(freeze())
            .withOnTick(spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F))
            .spawnIn(ZBiomeTags.WITH_FROST_ZOMBIES));
  }

  public static void register(
      Registerable<ZombieTemplate> registry, String id, ZombieTemplate.Builder builder) {
    registry.register(of(id), builder.build());
  }
}
