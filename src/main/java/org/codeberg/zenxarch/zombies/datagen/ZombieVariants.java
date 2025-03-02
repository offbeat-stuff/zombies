package org.codeberg.zenxarch.zombies.datagen;

import static org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect.*;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.spawn.BiomeSpawnCondition;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.ZombieEntityAttachments;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;
import org.codeberg.zenxarch.zombies.entity.variant.ZombieVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public interface ZombieVariants {
  public static RegistryKey<ZombieVariant> of(String path) {
    return RegistryKey.of(ZombieRegistryKeys.ZOMBIE_VARIANT, Zombies.id(path));
  }

  private static SpawnCondition condition(Registerable<ZombieVariant> registry, TagKey<Biome> tag) {
    var entryList = registry.getRegistryLookup(RegistryKeys.BIOME).getOrThrow(tag);
    return new BiomeSpawnCondition(entryList);
  }

  private static SpawnConditionSelectors condition(
      Registerable<ZombieVariant> registry, TagKey<Biome> tag, int weight) {
    return SpawnConditionSelectors.createSingle(condition(registry, tag), weight);
  }

  private static SpawnConditionSelectors condition(int weight) {
    return SpawnConditionSelectors.createFallback(weight);
  }

  public static final String COMMON_ZOMBIE = "default";
  public static final String SWAPPING_ZOMBIE = "swapping";
  public static final String FIRE_ZOMBIE = "fire";
  public static final String FREEZE_ZOMBIE = "freeze";

  static void bootstrap(Registerable<ZombieVariant> registry) {
    register(registry, COMMON_ZOMBIE, new ZombieVariantMapBuilder(), condition(512));
    register(
        registry,
        SWAPPING_ZOMBIE,
        new ZombieVariantMapBuilder()
            .with(ZombieEntityAttachments.ON_ATTACK, swapPositions())
            .with(ZombieEntityAttachments.ON_TICK, spawnParticles(ParticleTypes.PORTAL, 0.2F)),
        condition(1));

    register(
        registry,
        FIRE_ZOMBIE,
        new ZombieVariantMapBuilder()
            .with(ZombieEntityAttachments.ON_ATTACK, ignite(1.0F))
            .with(ZombieEntityAttachments.ON_TICK, spawnParticles(ParticleTypes.FLAME, 0.2F)),
        condition(registry, ZBiomeTags.WITH_FLAME_ZOMBIES, 16));

    register(
        registry,
        FREEZE_ZOMBIE,
        new ZombieVariantMapBuilder()
            .with(ZombieEntityAttachments.ON_ATTACK, freeze())
            .with(ZombieEntityAttachments.ON_TICK, spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F)),
        condition(registry, ZBiomeTags.WITH_FROST_ZOMBIES, 16));
  }

  public static void register(
      Registerable<ZombieVariant> registry,
      String id,
      ZombieVariantMapBuilder componentMap,
      SpawnConditionSelectors condition) {
    registry.register(of(id), new ZombieVariant(componentMap.build(), condition));
  }

  public static void register(
      Registerable<ZombieVariant> registry, String id, ZombieVariant variant) {
    registry.register(of(id), variant);
  }

  static class ZombieVariantMapBuilder {
    private Map<AttachmentType<?>, Object> componentMap = new HashMap<>();

    public <T> ZombieVariantMapBuilder with(AttachmentType<T> attachment, T value) {
      this.componentMap.put(attachment, value);
      return this;
    }

    public Map<AttachmentType<?>, Object> build() {
      return componentMap;
    }
  }
}
