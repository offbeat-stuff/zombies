package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;

public record IgniteEffect(float seconds) implements SingleLivingEffect {
  public static final MapCodec<IgniteEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(Codecs.POSITIVE_FLOAT.fieldOf("seconds").forGetter(IgniteEffect::seconds))
                  .apply(instance, IgniteEffect::new));

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    if (target.isFireImmune()) return;
    target.setOnFireFor(seconds);
  }

  @Override
  public MapCodec<? extends SingleLivingEffect> getCodec() {
    return CODEC;
  }
}
