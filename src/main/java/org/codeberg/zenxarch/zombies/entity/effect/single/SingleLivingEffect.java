package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;

public interface SingleLivingEffect {
  public static final Codec<SingleLivingEffect> CODEC =
      ZombieRegistries.SINGLE_LIVING_EFFECT_REGISTRY
          .getCodec()
          .dispatch(SingleLivingEffect::getCodec, Function.identity());

  public void run(ServerWorld world, LivingEntity target);

  public MapCodec<? extends SingleLivingEffect> getCodec();

  public static void init() {
    register("default", DefaultSingleLivingEffect.CODEC);
    register("freeze", FreezeEffect.CODEC);
    register("ignite", IgniteEffect.CODEC);
    register("spawn_effect_cloud", SpawnEffectCloudEffect.CODEC);
    register("spawn_particle_effect", SpawnParticleEffect.CODEC);
  }

  private static void register(String id, MapCodec<? extends SingleLivingEffect> codec) {
    Registry.register(ZombieRegistries.SINGLE_LIVING_EFFECT_REGISTRY, Zombies.id(id), codec);
  }
}
