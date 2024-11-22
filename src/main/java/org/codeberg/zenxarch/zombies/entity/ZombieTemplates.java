package org.codeberg.zenxarch.zombies.entity;

import static org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect.*;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BiomeTags;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public interface ZombieTemplates {
  private static LootTableBasedTemplate.Builder defaultBuilder() {
    return LootTableBasedTemplate.builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT);
  }

  private static LootTableBasedTemplate.Builder defaultBuilder(int weight) {
    return LootTableBasedTemplate.builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT)
        .withWeight(weight);
  }

  public static final String COMMON_ZOMBIE = ZombieRegistry.getDefaultId();
  public static final String SWAPPING_ZOMBIE = "SwappingZombie";
  public static final String FIRE_ZOMBIE = "FireZombie";
  public static final String FREEZE_ZOMBIE = "FreezeZombie";

  public static void initialize() {
    register(COMMON_ZOMBIE, defaultBuilder(512));
    register(
        SWAPPING_ZOMBIE,
        defaultBuilder()
            .withOnAttack(swapPositions())
            .withOnTick(spawnParticles(ParticleTypes.PORTAL, 0.2F)));
    register(
        FIRE_ZOMBIE,
        defaultBuilder(16)
            .withOnAttack(ignite(1.0F))
            .withOnTick(spawnParticles(ParticleTypes.FLAME, 0.2F))
            .spawnIn(BiomeTags.SPAWNS_WARM_VARIANT_FROGS));
    register(
        FREEZE_ZOMBIE,
        defaultBuilder(16)
            .withOnAttack(freeze())
            .withOnTick(spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F))
            .spawnIn(BiomeTags.SPAWNS_COLD_VARIANT_FROGS));
    ZombieRegistry.freezeRegistry();
  }

  public static void register(String id, LootTableBasedTemplate.Builder builder) {
    ZombieRegistry.register(id, builder.build());
  }
}
