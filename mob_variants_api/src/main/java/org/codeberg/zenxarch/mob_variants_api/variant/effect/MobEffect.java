package org.codeberg.zenxarch.mob_variants_api.variant.effect;

import static net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegisteries;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.AllOfMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.ConditionalSpawnEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.ConvertToEntityTypeEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.DefaultMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.HealFromDamage;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.IntervalMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.RandomMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.SingleMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.StatusMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.pair.SwapMobEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.FreezeEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.IgniteEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.SpawnParticleEffect;
import org.jetbrains.annotations.Nullable;

public interface MobEffect {
  public static final Codec<MobEffect> CODEC =
      Codec.withAlternative(
          MobRegisteries.MOB_EFFECT.getCodec().dispatch(MobEffect::getCodec, Function.identity()),
          DefaultMobEffect.CODEC.codec());

  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery);

  public MapCodec<? extends MobEffect> getCodec();

  public static void init() {
    register("default", DefaultMobEffect.CODEC);
    register("all_of", AllOfMobEffect.CODEC);
    register("random", RandomMobEffect.CODEC);
    register("single", SingleMobEffect.CODEC);
    register("status_effect", StatusMobEffect.CODEC);
    register("swap", SwapMobEffect.CODEC);
    register("conditional", ConditionalSpawnEffect.CODEC);
    register("heal", HealFromDamage.CODEC);
    register("convert_to", ConvertToEntityTypeEffect.CODEC);
    register("interval", IntervalMobEffect.CODEC);
  }

  private static void register(String id, MapCodec<? extends MobEffect> codec) {
    Registry.register(MobRegisteries.MOB_EFFECT, MobVariantsApiMod.id(id), codec);
  }

  public static SwapMobEffect swapPositions() {
    return SwapMobEffect.create();
  }

  public static SingleMobEffect toPlayer(LivingEffect effect) {
    return new SingleMobEffect(effect, false);
  }

  public static SingleMobEffect toMob(LivingEffect effect) {
    return new SingleMobEffect(effect, true);
  }

  public static SingleMobEffect ignite(float seconds) {
    return toPlayer(new IgniteEffect(seconds));
  }

  public static SingleMobEffect freeze() {
    return toPlayer(FreezeEffect.create());
  }

  public static MobEffect random(MobEffect... effect) {
    return RandomMobEffect.create(effect);
  }

  public static MobEffect statusEffect(StatusEffectInstance... builders) {
    if (builders.length == 0) return DefaultMobEffect.create();
    return new StatusMobEffect(List.of(builders));
  }

  public static SingleMobEffect spawnParticles(
      ParticleEffect particle,
      SpawnParticlesEnchantmentEffect.PositionSource horizontalPosition,
      SpawnParticlesEnchantmentEffect.PositionSource verticalPosition,
      SpawnParticlesEnchantmentEffect.VelocitySource horizontalVelocity,
      SpawnParticlesEnchantmentEffect.VelocitySource verticalVelocity,
      FloatProvider speed) {
    return toMob(
        new SpawnParticleEffect(
            new SpawnParticlesEnchantmentEffect(
                particle,
                horizontalPosition,
                verticalPosition,
                horizontalVelocity,
                verticalVelocity,
                speed)));
  }

  public static SingleMobEffect spawnParticles(ParticleEffect particle, float speed) {
    var floatProvider = ConstantFloatProvider.create(speed);
    return spawnParticles(
        particle,
        withinBoundingBox(),
        withinBoundingBox(),
        fixedVelocity(floatProvider),
        fixedVelocity(floatProvider),
        floatProvider);
  }
}
