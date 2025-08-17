package org.codeberg.zenxarch.zombies.variant.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.variant.effect.LivingEffect;

public record DefaultLivingEffect() implements LivingEffect {

  public static final DefaultLivingEffect INSTANCE = new DefaultLivingEffect();
  public static final MapCodec<DefaultLivingEffect> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    /* No op */
  }

  @Override
  public MapCodec<DefaultLivingEffect> getCodec() {
    return CODEC;
  }
}
