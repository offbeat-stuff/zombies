package org.codeberg.zenxarch.mob_variants_api.variant.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegisteries;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.single.*;

public interface LivingEffect {
  public static final Codec<LivingEffect> CODEC =
      MobRegisteries.LIVING_EFFECT.getCodec().dispatch(LivingEffect::getCodec, Function.identity());

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
    register("explosion", ExplosionEffect.CODEC);
    register("bonemeal", BonemealLivingEffect.CODEC);
  }

  private static void register(String id, MapCodec<? extends LivingEffect> codec) {
    Registry.register(MobRegisteries.LIVING_EFFECT, MobVariantsApiMod.id(id), codec);
  }
}
