package org.codeberg.zenxarch.zombies.entity.effect;

import static net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect.*;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Function;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.effect.single.FreezeEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.IgniteEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.SpawnParticleEffect;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;
import org.jetbrains.annotations.Nullable;

public interface ZombieEffect {
  public static final Codec<ZombieEffect> CODEC =
      Codec.withAlternative(
          ZombieRegistries.ZOMBIE_EFFECT_REGISTRY
              .getCodec()
              .dispatch(ZombieEffect::getCodec, Function.identity()),
          DefaultZombieEffect.CODEC.codec());

  public void run(ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery);

  public MapCodec<? extends ZombieEffect> getCodec();

  public static void init() {
    register("default", DefaultZombieEffect.CODEC);
    register("all_of", AllOfZombieEffect.CODEC);
    register("random", RandomZombieEffect.CODEC);
    register("single_target", SingleTargetZombieEffect.CODEC);
    register("status_effect", StatusEffectZombieEffect.CODEC);
    register("swap_position", SwapPositionZombieEffect.CODEC);
  }

  private static void register(String id, MapCodec<? extends ZombieEffect> codec) {
    Registry.register(ZombieRegistries.ZOMBIE_EFFECT_REGISTRY, Zombies.id(id), codec);
  }

  public static SwapPositionZombieEffect swapPositions() {
    return SwapPositionZombieEffect.create();
  }

  public static SingleTargetZombieEffect toPlayer(SingleLivingEffect effect) {
    return new SingleTargetZombieEffect(effect, false);
  }

  public static SingleTargetZombieEffect toZombie(SingleLivingEffect effect) {
    return new SingleTargetZombieEffect(effect, true);
  }

  public static SingleTargetZombieEffect ignite(float seconds) {
    return toPlayer(new IgniteEffect(seconds));
  }

  public static SingleTargetZombieEffect freeze() {
    return toPlayer(FreezeEffect.create());
  }

  public static ZombieEffect random(ZombieEffect... effect) {
    return RandomZombieEffect.create(effect);
  }

  public static ZombieEffect statusEffect(StatusEffectInstance... builders) {
    if (builders.length == 0) return DefaultZombieEffect.create();
    return new StatusEffectZombieEffect(List.of(builders));
  }

  public static SingleTargetZombieEffect spawnParticles(
      ParticleEffect particle,
      SpawnParticlesEnchantmentEffect.PositionSource horizontalPosition,
      SpawnParticlesEnchantmentEffect.PositionSource verticalPosition,
      SpawnParticlesEnchantmentEffect.VelocitySource horizontalVelocity,
      SpawnParticlesEnchantmentEffect.VelocitySource verticalVelocity,
      FloatProvider speed) {
    return toZombie(
        new SpawnParticleEffect(
            new SpawnParticlesEnchantmentEffect(
                particle,
                horizontalPosition,
                verticalPosition,
                horizontalVelocity,
                verticalVelocity,
                speed)));
  }

  public static SingleTargetZombieEffect spawnParticles(ParticleEffect particle, float speed) {
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
