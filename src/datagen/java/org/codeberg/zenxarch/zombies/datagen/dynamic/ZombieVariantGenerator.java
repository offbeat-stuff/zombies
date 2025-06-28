package org.codeberg.zenxarch.zombies.datagen.dynamic;

import static org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.spawn.BiomeSpawnCondition;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;
import org.codeberg.zenxarch.zombies.data.entity.MobAttachments;
import org.codeberg.zenxarch.zombies.data.entity.effect.*;
import org.codeberg.zenxarch.zombies.data.entity.effect.pair.AllOfMobEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.pair.ConditionalSpawnEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.pair.RandomMobEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.pair.SingleMobEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.single.DefaultAttributeEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.single.SpawnEffectCloudEffect;
import org.codeberg.zenxarch.zombies.data.spawn_conditions.DaySpawnCondition;
import org.codeberg.zenxarch.zombies.data.spawn_conditions.NightSpawnCondition;
import org.codeberg.zenxarch.zombies.datagen.provider.ZLootTableProvider;
import org.codeberg.zenxarch.zombies.entity.variant.MobVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public final class ZombieVariantGenerator {

  public static final DynamicRegistryInitializer<MobVariant> INITIALIZER =
      new DynamicRegistryInitializer<>(
          ZombieRegistryKeys.MOB_VARIANT, ZombieVariantGenerator::bootstrap);

  private static SpawnCondition condition(Registerable<MobVariant> registry, TagKey<Biome> tag) {
    var entryList = registry.getRegistryLookup(RegistryKeys.BIOME).getOrThrow(tag);
    return new BiomeSpawnCondition(entryList);
  }

  private static SpawnConditionSelectors condition(
      Registerable<MobVariant> registry, TagKey<Biome> tag, int weight) {
    return SpawnConditionSelectors.createSingle(condition(registry, tag), weight);
  }

  private static SpawnConditionSelectors condition(int weight) {
    return SpawnConditionSelectors.createFallback(weight);
  }

  public static final RegistryKey<MobVariant> COMMON = INITIALIZER.of("default");
  public static final RegistryKey<MobVariant> AXE = INITIALIZER.of("axe");
  public static final RegistryKey<MobVariant> SWAPPING = INITIALIZER.of("swapping");
  public static final RegistryKey<MobVariant> FIRE = INITIALIZER.of("fire");
  public static final RegistryKey<MobVariant> FREEZE = INITIALIZER.of("freeze");
  public static final RegistryKey<MobVariant> SWAMP = INITIALIZER.of("swamp");

  private static MobEffect createAttributeEffect(
      RegistryEntry<EntityAttribute> attribute, FloatProvider value) {
    return new SingleMobEffect(new DefaultAttributeEffect(attribute, value), true);
  }

  private static MobEffect attributesOnSpawn() {
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

    var optionalEffect = AllOfMobEffect.create(halfHealthEffect, doubleSpeedEffect);

    return AllOfMobEffect.create(
        dayFollowRangeEffect,
        nightFollowRangeEffect,
        RandomMobEffect.create(ConstantFloatProvider.create(0.8f), optionalEffect));
  }

  private static ZombieVariantMapBuilder defaultAttributeMap() {
    return new ZombieVariantMapBuilder().with(MobAttachments.ON_SPAWN, attributesOnSpawn());
  }

  private static ZombieVariantMapBuilder defaultEquipmentMap() {
    return defaultAttributeMap()
        .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.COMMON_ZOMBIE_EQUIPMENT);
  }

  public static void bootstrap(Registerable<MobVariant> registry) {
    register(registry, COMMON, defaultEquipmentMap(), condition(512));
    register(
        registry,
        SWAPPING,
        defaultEquipmentMap()
            .with(MobAttachments.ON_ATTACK, swapPositions())
            .with(MobAttachments.ON_TICK, spawnParticles(ParticleTypes.PORTAL, 0.2F)),
        condition(1));

    register(
        registry,
        FIRE,
        defaultEquipmentMap()
            .with(MobAttachments.ON_ATTACK, ignite(1.0F))
            .with(MobAttachments.ON_TICK, spawnParticles(ParticleTypes.FLAME, 0.2F)),
        condition(registry, ZBiomeTags.WITH_FLAME_ZOMBIES, 16));

    register(
        registry,
        FREEZE,
        defaultEquipmentMap()
            .with(MobAttachments.ON_ATTACK, freeze())
            .with(MobAttachments.ON_TICK, spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F)),
        condition(registry, ZBiomeTags.WITH_FROST_ZOMBIES, 16));

    register(
        registry,
        AXE,
        defaultAttributeMap()
            .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.AXE_ZOMBIE_EQUIPMENT),
        condition(registry, ZBiomeTags.WITH_AXE_ZOMBIES, 128));
    var poisonEffectParticle =
        StatusEffects.POISON.value().createParticle(new StatusEffectInstance(StatusEffects.POISON));
    register(
        registry,
        SWAMP,
        defaultEquipmentMap()
            .with(
                MobAttachments.ON_ATTACK,
                statusEffect(new StatusEffectInstance(StatusEffects.POISON, 80)))
            .with(MobAttachments.ON_TICK, spawnParticles(poisonEffectParticle, 0.2F))
            .with(
                MobAttachments.ON_KILLED,
                new SingleMobEffect(
                    new SpawnEffectCloudEffect(
                        List.of(new StatusEffectInstance(StatusEffects.POISON, 200)),
                        poisonEffectParticle,
                        5.0F,
                        2.0F,
                        200),
                    true)),
        condition(registry, ZBiomeTags.WITH_SWAMP_ZOMBIES, 16));
  }

  private static void register(
      Registerable<MobVariant> registry,
      RegistryKey<MobVariant> key,
      ZombieVariantMapBuilder componentMap,
      SpawnConditionSelectors condition) {
    registry.register(key, new MobVariant(componentMap.build(), condition));
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
