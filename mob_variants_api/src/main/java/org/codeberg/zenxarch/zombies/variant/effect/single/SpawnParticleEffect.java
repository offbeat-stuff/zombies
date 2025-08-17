package org.codeberg.zenxarch.zombies.variant.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.variant.effect.LivingEffect;

public record SpawnParticleEffect(SpawnParticlesEnchantmentEffect effect) implements LivingEffect {

  public static final MapCodec<SpawnParticleEffect> CODEC =
      SpawnParticlesEnchantmentEffect.CODEC.xmap(
          SpawnParticleEffect::new, SpawnParticleEffect::effect);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    effect.apply(world, 0, null, target, target.getPos());
  }

  @Override
  public MapCodec<SpawnParticleEffect> getCodec() {
    return CODEC;
  }
}
