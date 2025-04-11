package org.codeberg.zenxarch.zombies.data.entity.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;

public record FreezeEffect() implements LivingEffect {

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
