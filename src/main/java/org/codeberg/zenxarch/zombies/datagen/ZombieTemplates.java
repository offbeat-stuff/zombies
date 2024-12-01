package org.codeberg.zenxarch.zombies.datagen;

import static org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect.*;

import java.util.function.Consumer;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;
import org.codeberg.zenxarch.zombies.entity.ZombieEvents;
import org.codeberg.zenxarch.zombies.entity.ZombieTemplate;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;

public interface ZombieTemplates {
  public static RegistryKey<ZombieTemplate> of(String path) {
    return RegistryKey.of(ZombieRegistries.TEMPLATE_REGISTRY_KEY, Zombies.id(path));
  }

  private static ZombieTemplate.Builder builder(Consumer<ZombieEvents.Builder> builder) {
    return ZombieTemplate.builder(builder);
  }

  private static ZombieTemplate.Builder builder(
      Consumer<ZombieEvents.Builder> builder, int weight) {
    return ZombieTemplate.builder(builder).withWeight(weight);
  }

  public static final String COMMON_ZOMBIE = "default";
  public static final String SWAPPING_ZOMBIE = "swapping";
  public static final String FIRE_ZOMBIE = "fire";
  public static final String FREEZE_ZOMBIE = "freeze";

  static void bootstrap(Registerable<ZombieTemplate> registry) {
    register(registry, COMMON_ZOMBIE, builder(builder -> {}, 512));
    register(
        registry,
        SWAPPING_ZOMBIE,
        builder(
            builder ->
                builder
                    .onAttack(swapPositions())
                    .onTick(spawnParticles(ParticleTypes.PORTAL, 0.2F))));
    register(
        registry,
        FIRE_ZOMBIE,
        builder(
                builder ->
                    builder
                        .onAttack(ignite(1.0F))
                        .onTick(spawnParticles(ParticleTypes.FLAME, 0.2F)),
                16)
            .spawnIn(ZBiomeTags.WITH_FLAME_ZOMBIES));
    register(
        registry,
        FREEZE_ZOMBIE,
        builder(
                builder ->
                    builder
                        .onAttack(freeze())
                        .onTick(spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F)),
                16)
            .spawnIn(ZBiomeTags.WITH_FROST_ZOMBIES));
  }

  public static void register(
      Registerable<ZombieTemplate> registry, String id, ZombieTemplate.Builder builder) {
    registry.register(of(id), builder.build());
  }
}
