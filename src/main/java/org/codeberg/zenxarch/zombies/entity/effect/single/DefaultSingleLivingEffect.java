package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public record DefaultSingleLivingEffect() implements SingleLivingEffect {

  public static final DefaultSingleLivingEffect INSTANCE = new DefaultSingleLivingEffect();
  public static final MapCodec<DefaultSingleLivingEffect> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    /* No op */
  }

  @Override
  public MapCodec<DefaultSingleLivingEffect> getCodec() {
    return CODEC;
  }
}
