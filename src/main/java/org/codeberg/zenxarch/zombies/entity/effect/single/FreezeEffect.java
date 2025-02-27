package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public record FreezeEffect() implements SingleLivingEffect {

  private static final FreezeEffect INSTANCE = new FreezeEffect();
  public static final MapCodec<FreezeEffect> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    if (!target.canFreeze()) return;
    target.setFrozenTicks(target.getMinFreezeDamageTicks());
  }

  public static FreezeEffect create() {
    return INSTANCE;
  }

  @Override
  public MapCodec<FreezeEffect> getCodec() {
    return CODEC;
  }
}
