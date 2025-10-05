package org.codeberg.zenxarch.default_zombies.datagen.dynamic;

import static org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
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
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.default_zombies.datagen.provider.ZEntityLootTableProvider;
import org.codeberg.zenxarch.default_zombies.datagen.provider.ZLootTableProvider;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;
import org.codeberg.zenxarch.mob_variants_api.spawn_conditions.RainingSpawnCondition;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.*;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.AllOfMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.HealFromDamage;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.IntervalMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.RandomMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.SingleMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.StatusMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.AttributeModifierEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.BonemealLivingEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.DefaultAttributeEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.ExplosionEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.SpawnEffectCloudEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.StatusLivingEffect;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.client.OverlayClient;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;

public final class ZombieVariantGenerator {

  public static final DynamicRegistryInitializer<MobVariant> INITIALIZER =
      new DynamicRegistryInitializer<>(
          MobRegistryKeys.MOB_VARIANT, ZombieVariantGenerator::bootstrap);

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

  public static final RegistryKey<MobVariant> LOOT_TEMPLATE = INITIALIZER.of("template/loot");
  public static final RegistryKey<MobVariant> ATTRIBUTE_TEMPLATE =
      INITIALIZER.of("template/attribute");

  public static final RegistryKey<MobVariant> COMMON = INITIALIZER.of("default");
  public static final RegistryKey<MobVariant> AXE = INITIALIZER.of("axe");
  public static final RegistryKey<MobVariant> SWAPPING = INITIALIZER.of("swapping");
  public static final RegistryKey<MobVariant> FIRE = INITIALIZER.of("fire");
  public static final RegistryKey<MobVariant> FREEZE = INITIALIZER.of("freeze");
  public static final RegistryKey<MobVariant> SWAMP = INITIALIZER.of("swamp");
  public static final RegistryKey<MobVariant> DESERT = INITIALIZER.of("desert");
  public static final RegistryKey<MobVariant> RAIN = INITIALIZER.of("rain");
  public static final RegistryKey<MobVariant> EXPLOSION = INITIALIZER.of("explosion");
  public static final RegistryKey<MobVariant> INVISIBLE = INITIALIZER.of("invisible");
  public static final RegistryKey<MobVariant> BONEMEAL = INITIALIZER.of("bonemeal");
  public static final RegistryKey<MobVariant> OAK_DOOR = INITIALIZER.of("oak_door");
  public static final RegistryKey<MobVariant> COPPER_DOOR = INITIALIZER.of("copper_door");
  public static final RegistryKey<MobVariant> IRON_DOOR = INITIALIZER.of("iron_door");
  public static final RegistryKey<MobVariant> HEADLESS = INITIALIZER.of("headless");

  // public static final RegistryKey<MobVariant> INK_ATTACK = INITIALIZER.of("ink_attack");

  public static List<RegistryKey<MobVariant>> ALL =
      List.of(
          COMMON,
          AXE,
          SWAPPING,
          FIRE,
          FREEZE,
          SWAMP,
          DESERT,
          RAIN,
          EXPLOSION,
          INVISIBLE,
          BONEMEAL,
          OAK_DOOR,
          COPPER_DOOR,
          IRON_DOOR,
          HEADLESS);

  private static MobEffect createAttributeEffect(
      RegistryEntry<EntityAttribute> attribute, FloatProvider value) {
    return new SingleMobEffect(new DefaultAttributeEffect(attribute, value), true);
  }

  private static MobEffect createAttributeModifierEffect(
      RegistryEntry<EntityAttribute> attribute, String id, double value, Operation op) {
    return new SingleMobEffect(
        new AttributeModifierEffect(
            attribute, List.of(new EntityAttributeModifier(Zombies.id(id), value, op)), true),
        true);
  }

  private static MobEffect attributesOnSpawn() {
    var commonFollowRange = ClampedNormalFloatProvider.create(10f, 5f, 8f, 14f);
    var uncommonFollowRange = ClampedNormalFloatProvider.create(20f, 8f, 18f, 25f);
    var rareFollowRange = ClampedNormalFloatProvider.create(32f, 8f, 28f, 36f);

    var commonFollowRangeEffect =
        createAttributeEffect(EntityAttributes.FOLLOW_RANGE, commonFollowRange);
    var uncommonFollowRangeEffect =
        createAttributeEffect(EntityAttributes.FOLLOW_RANGE, uncommonFollowRange);
    var rareFollowRangeEffect =
        createAttributeEffect(EntityAttributes.FOLLOW_RANGE, rareFollowRange);

    var extraHealthEffect =
        createAttributeEffect(
            EntityAttributes.MAX_HEALTH, ClampedNormalFloatProvider.create(40f, 10f, 32f, 48f));
    var lowSpeedEffect =
        createAttributeModifierEffect(
            EntityAttributes.MOVEMENT_SPEED, "zombie_speed", -0.5, Operation.ADD_MULTIPLIED_TOTAL);

    var lowHealthEffect =
        createAttributeEffect(
            EntityAttributes.MAX_HEALTH, ClampedNormalFloatProvider.create(24f, 8f, 20f, 28f));
    var highSpeedEffect =
        createAttributeModifierEffect(
            EntityAttributes.MOVEMENT_SPEED, "zombie_speed", 0.1, Operation.ADD_MULTIPLIED_TOTAL);

    return AllOfMobEffect.create(
        commonFollowRangeEffect,
        RandomMobEffect.create(ConstantFloatProvider.create(0.05f), uncommonFollowRangeEffect),
        AllOfMobEffect.create(extraHealthEffect, lowSpeedEffect),
        RandomMobEffect.create(
            ConstantFloatProvider.create(0.001f),
            AllOfMobEffect.create(rareFollowRangeEffect, lowHealthEffect, highSpeedEffect)));
  }

  private static ZombieVariantMapBuilder defaultEffectMap(
      ZombieVariantMapBuilder original, List<RegistryEntry<StatusEffect>> effects) {
    var effectInstances = effects.stream().map(e -> new StatusEffectInstance(e, 80)).toList();
    var particle = effects.get(0).value().createParticle(effectInstances.get(0));
    return original
        .with(MobAttachments.ON_ATTACK, new StatusMobEffect(effectInstances))
        .with(MobAttachments.ON_TICK, spawnParticles(particle, 0.2F));
  }

  private static ZombieVariantMapBuilder spawnWithEffects(
      ZombieVariantMapBuilder original, List<RegistryEntry<StatusEffect>> effects) {
    var effectInstances = effects.stream().map(e -> new StatusEffectInstance(e, -1)).toList();
    var particle = effects.get(0).value().createParticle(effectInstances.get(0));
    return original
        .with(
            MobAttachments.ON_SPAWN,
            new SingleMobEffect(new StatusLivingEffect(effectInstances), true))
        .with(MobAttachments.ON_TICK, spawnParticles(particle, 0.2F));
  }

  private static ZombieVariantMapBuilder defaultEffectMapWithSpawnCloud(
      ZombieVariantMapBuilder original, List<RegistryEntry<StatusEffect>> effects) {
    var effectInstance = new StatusEffectInstance(effects.get(0), 200);
    var particle = effects.get(0).value().createParticle(effectInstance);
    return defaultEffectMap(original, effects)
        .with(
            MobAttachments.ON_KILLED,
            new SingleMobEffect(
                new SpawnEffectCloudEffect(List.of(effectInstance), particle, 5.0F, 2.0F, 200),
                true));
  }

  private static final int COMMON_WEIGHT = 512;
  private static final int UNCOMMON_WEIGHT = 128;
  private static final int RARE_WEIGHT = 64;

  public static void bootstrap(Registerable<MobVariant> registry) {
    var lookup = registry.getRegistryLookup(RegistryKeys.DAMAGE_TYPE);

    register(
        registry,
        LOOT_TEMPLATE,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.COMMON_ZOMBIE_EQUIPMENT)
            .with(MobAttachments.LOOT_TABLE, ZEntityLootTableProvider.ZOMBIE_DROPS),
        condition(0));

    register(
        registry,
        ATTRIBUTE_TEMPLATE,
        new ZombieVariantMapBuilder().with(MobAttachments.ON_SPAWN, attributesOnSpawn()),
        condition(0));

    register(
        registry,
        COMMON,
        new ZombieVariantMapBuilder(),
        condition(COMMON_WEIGHT),
        LOOT_TEMPLATE,
        ATTRIBUTE_TEMPLATE);
    register(
        registry,
        SWAPPING,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.ON_ATTACK, swapPositions())
            .with(MobAttachments.ON_TICK, spawnParticles(ParticleTypes.PORTAL, 0.2F)),
        condition(RARE_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        FIRE,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.ON_ATTACK, ignite(1.0F))
            .with(MobAttachments.ON_TICK, spawnParticles(ParticleTypes.FLAME, 0.2F))
            .with(MobAttachments.INVULNERABLE_TO, lookup.getOrThrow(DamageTypeTags.IS_FIRE)),
        condition(registry, ZBiomeTags.WITH_FLAME_ZOMBIES, RARE_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        FREEZE,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.ON_ATTACK, freeze())
            .with(MobAttachments.ON_TICK, spawnParticles(ParticleTypes.SNOWFLAKE, 0.2F))
            .with(MobAttachments.INVULNERABLE_TO, lookup.getOrThrow(DamageTypeTags.IS_FREEZING)),
        condition(registry, ZBiomeTags.WITH_FROST_ZOMBIES, RARE_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        AXE,
        new ZombieVariantMapBuilder()
            .with(
                MobAttachments.ON_ATTACK,
                AllOfMobEffect.create(
                    HealFromDamage.INSTANCE, spawnParticles(ParticleTypes.HEART, 1.0F)))
            .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.AXE_ZOMBIE_EQUIPMENT),
        condition(registry, ZBiomeTags.WITH_AXE_ZOMBIES, UNCOMMON_WEIGHT),
        LOOT_TEMPLATE,
        ATTRIBUTE_TEMPLATE);
    register(
        registry,
        SWAMP,
        defaultEffectMapWithSpawnCloud(
            new ZombieVariantMapBuilder(), List.of(StatusEffects.POISON)),
        condition(registry, ZBiomeTags.WITH_SWAMP_ZOMBIES, RARE_WEIGHT),
        LOOT_TEMPLATE);
    register(
        registry,
        DESERT,
        defaultEffectMap(
                new ZombieVariantMapBuilder(),
                List.of(StatusEffects.DARKNESS, StatusEffects.HUNGER))
            .with(
                MobAttachments.TEXTURE_OVERRIDE,
                new TextureAssetInfo(Identifier.ofVanilla("entity/zombie/husk"))),
        condition(registry, ZBiomeTags.WITH_DESERT_ZOMBIES, RARE_WEIGHT),
        LOOT_TEMPLATE,
        ATTRIBUTE_TEMPLATE);
    register(
        registry,
        RAIN,
        new ZombieVariantMapBuilder()
            .with(
                MobAttachments.TEXTURE_OVERRIDE,
                new TextureAssetInfo(Identifier.of("entity/zombie/drowned")))
            .with(MobAttachments.LOOT_TABLE, EntityType.DROWNED.getLootTableKey().get())
            .with(
                MobAttachments.OVERLAY,
                OverlayClient.make(
                    EntityModelLayers.DROWNED_OUTER,
                    new TextureAssetInfo(
                        Identifier.ofVanilla("entity/zombie/drowned_outer_layer")))),
        SpawnConditionSelectors.createSingle(RainingSpawnCondition.INSTANCE, UNCOMMON_WEIGHT),
        LOOT_TEMPLATE);
    register(
        registry,
        EXPLOSION,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.ON_ATTACK, new SingleMobEffect(new ExplosionEffect(3.0F), true)),
        condition(RARE_WEIGHT),
        LOOT_TEMPLATE);
    register(
        registry,
        INVISIBLE,
        spawnWithEffects(new ZombieVariantMapBuilder(), List.of(StatusEffects.INVISIBILITY)),
        condition(RARE_WEIGHT),
        LOOT_TEMPLATE);
    register(
        registry,
        BONEMEAL,
        new ZombieVariantMapBuilder()
            .with(
                MobAttachments.ON_TICK,
                AllOfMobEffect.create(
                    new IntervalMobEffect(
                        20, new SingleMobEffect(new BonemealLivingEffect(), true)),
                    spawnParticles(ParticleTypes.HAPPY_VILLAGER, 0.2F))),
        condition(registry, ZBiomeTags.WITH_AXE_ZOMBIES, RARE_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        OAK_DOOR,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.OAK_DOOR_SHIELD_EQUIPMENT),
        condition(UNCOMMON_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        COPPER_DOOR,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.COPPER_DOOR_SHIELD_EQUIPMENT),
        condition(RARE_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        IRON_DOOR,
        new ZombieVariantMapBuilder()
            .with(MobAttachments.EQUIPMENT_TABLE, ZLootTableProvider.IRON_DOOR_SHIELD_EQUIPMENT),
        condition(RARE_WEIGHT),
        LOOT_TEMPLATE);

    register(
        registry,
        HEADLESS,
        new ZombieVariantMapBuilder().with(MobAttachments.RENDER_HEAD, false),
        condition(UNCOMMON_WEIGHT),
        LOOT_TEMPLATE,
        ATTRIBUTE_TEMPLATE);

    // register(
    //     registry,
    //     INK_ATTACK,
    //     defaultAttributeMap()
    //         .with(
    //             MobAttachments.ON_ATTACK,
    //             new SingleMobEffect(
    //                 new SpawnParticleEffect(
    //                     new SpawnParticlesEnchantmentEffect(
    //                         ParticleTypes.SQUID_INK,
    //                         SpawnParticlesEnchantmentEffect.withinBoundingBox(),
    //                         SpawnParticlesEnchantmentEffect.withinBoundingBox(),
    //                         SpawnParticlesEnchantmentEffect.scaledVelocity(0.1f),
    //                         SpawnParticlesEnchantmentEffect.fixedVelocity(
    //                             UniformFloatProvider.create(0.01f, 0.05f)),
    //                         ConstantFloatProvider.create(0.05f)),
    //                     450),
    //                 false)),
    //     condition(UNCOMMON_WEIGHT));
  }

  private static void register(
      Registerable<MobVariant> registry,
      RegistryKey<MobVariant> key,
      ZombieVariantMapBuilder componentMap,
      SpawnConditionSelectors condition) {
    registry.register(key, new MobVariant(componentMap.build(), condition));
  }

  @SafeVarargs
  private static void register(
      Registerable<MobVariant> registry,
      RegistryKey<MobVariant> key,
      ZombieVariantMapBuilder componentMap,
      SpawnConditionSelectors condition,
      RegistryKey<MobVariant>... templates) {
    var mobVariantRegistry = registry.getRegistryLookup(MobRegistryKeys.MOB_VARIANT);
    var templateEntries =
        List.of(templates).stream()
            .map(mobVariantRegistry::getOrThrow)
            .map(t -> (RegistryEntry<MobVariant>) t)
            .toList();
    registry.register(key, new MobVariant(componentMap.build(), condition, templateEntries));
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
