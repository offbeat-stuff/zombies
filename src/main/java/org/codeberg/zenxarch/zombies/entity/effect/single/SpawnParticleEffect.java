package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public record SpawnParticleEffect(SpawnParticlesEnchantmentEffect effect)
    implements SingleLivingEffect {

  public static final MapCodec<SpawnParticleEffect> CODEC =
      SpawnParticlesEnchantmentEffect.CODEC.xmap(
          SpawnParticleEffect::new, SpawnParticleEffect::effect);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    effect.apply(world, 0, null, target, target.getPos());
  }

  @Override
  public MapCodec<? extends SingleLivingEffect> getCodec() {
    return CODEC;
  }
}
