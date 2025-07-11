package org.codeberg.zenxarch.zombies.data.entity.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.entity.effect.single.*;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;

public interface LivingEffect {
  public static final Codec<LivingEffect> CODEC =
      ZombieRegistries.LIVING_EFFECT
          .getCodec()
          .dispatch(LivingEffect::getCodec, Function.identity());

  public void run(ServerWorld world, LivingEntity target);

  public MapCodec<? extends LivingEffect> getCodec();

  public static void init() {
    register("default", DefaultLivingEffect.CODEC);
    register("freeze", FreezeEffect.CODEC);
    register("ignite", IgniteEffect.CODEC);
    register("spawn_effect_cloud", SpawnEffectCloudEffect.CODEC);
    register("spawn_particle", SpawnParticleEffect.CODEC);
    register("apply_status_effect", StatusLivingEffect.CODEC);
    register("default_attribute", DefaultAttributeEffect.CODEC);
    register("attribute_modifier", AttributeModifierEffect.CODEC);
  }

  private static void register(String id, MapCodec<? extends LivingEffect> codec) {
    Registry.register(ZombieRegistries.LIVING_EFFECT, Zombies.id(id), codec);
  }
}
