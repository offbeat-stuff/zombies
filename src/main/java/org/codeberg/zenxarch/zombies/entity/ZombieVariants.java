package org.codeberg.zenxarch.zombies.entity;

import static org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.spawn.BiomeSpawnCondition;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;
import org.codeberg.zenxarch.zombies.entity.effect.AllOfZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.ConditionalSpawnEffect;
import org.codeberg.zenxarch.zombies.entity.effect.DefaultZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.RandomZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.SingleTargetZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.DefaultAttributeEffect;
import org.codeberg.zenxarch.zombies.entity.spawn_conditions.DaySpawnCondition;
import org.codeberg.zenxarch.zombies.entity.spawn_conditions.NightSpawnCondition;
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

  private static ZombieEffect createAttributeEffect(
      RegistryEntry<EntityAttribute> attribute, FloatProvider value) {
    return new SingleTargetZombieEffect(new DefaultAttributeEffect(attribute, value), true);
  }

  private static ZombieEffect attributesOnSpawn() {
    var dayFollowRange = ClampedNormalFloatProvider.create(20.0f, 8.0f, 14.0f, 26.0f);
    var nightFollowRange = ClampedNormalFloatProvider.create(32.0f, 8.0f, 26.0f, 38.0f);
    var dayFollowRangeEffect =
        new ConditionalSpawnEffect(
            DaySpawnCondition.INSTANCE,
            createAttributeEffect(EntityAttributes.FOLLOW_RANGE, dayFollowRange),
            true);
    var nightFollowRangeEffect =
        new ConditionalSpawnEffect(
            NightSpawnCondition.INSTANCE,
            createAttributeEffect(EntityAttributes.FOLLOW_RANGE, nightFollowRange),
            true);
    var halfHealthEffect =
        createAttributeEffect(EntityAttributes.MAX_HEALTH, ConstantFloatProvider.create(10.0f));
    var doubleSpeedEffect =
        new ConditionalSpawnEffect(
            NightSpawnCondition.INSTANCE,
            createAttributeEffect(
                EntityAttributes.MOVEMENT_SPEED, ConstantFloatProvider.create(0.46f)),
            true);

    var optionalEffect = AllOfZombieEffect.create(halfHealthEffect, doubleSpeedEffect);

    return AllOfZombieEffect.create(
        dayFollowRangeEffect,
        nightFollowRangeEffect,
        RandomZombieEffect.create(
            DefaultZombieEffect.create(),
            optionalEffect,
            optionalEffect,
            optionalEffect,
            optionalEffect));
  }

  public static void bootstrap(Registerable<ZombieVariant> registry) {
    var attributesOnSpawn = attributesOnSpawn();
    register(
        registry,
        COMMON_ZOMBIE,
        new ZombieVariantMapBuilder().with(ZombieEntityAttachments.ON_SPAWN, attributesOnSpawn),
        condition(512));
    register(
        registry,
        SWAPPING_ZOMBIE,
        new ZombieVariantMapBuilder()
            .with(ZombieEntityAttachments.ON_ATTACK, swapPositions())
            .with(ZombieEntityAttachments.ON_TICK, spawnParticles(ParticleTypes.PORTAL, 0.2F))
            .with(ZombieEntityAttachments.ON_SPAWN, attributesOnSpawn),
        condition(1));

    register(
        registry,
        FIRE_ZOMBIE,
        new ZombieVariantMapBuilder()
            .with(ZombieEntityAttachments.ON_ATTACK, ignite(1.0F))
            .with(ZombieEntityAttachments.ON_TICK, spawnParticles(ParticleTypes.FLAME, 0.2F))
            .with(ZombieEntityAttachments.ON_SPAWN, attributesOnSpawn),
        condition(registry, ZBiomeTags.WITH_FLAME_ZOMBIES, 16));

    register(
        registry,
        FREEZE_ZOMBIE,
        new ZombieVariantMapBuilder()
            .with(ZombieEntityAttachments.ON_ATTACK, freeze())
            .with(ZombieEntityAttachments.ON_TICK, spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F))
            .with(ZombieEntityAttachments.ON_SPAWN, attributesOnSpawn),
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

  public static Optional<RegistryEntry.Reference<ZombieVariant>> getRandomVariantFromPos(
      ServerWorld world, BlockPos pos) {
    return VariantSelectorProvider.select(
        world.getRegistryManager().getOrThrow(ZombieRegistryKeys.ZOMBIE_VARIANT).streamEntries(),
        RegistryEntry::value,
        world.getRandom(),
        SpawnContext.of(world, pos));
  }
}
