package org.codeberg.zenxarch.zombies.data.entity.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;

public record IgniteEffect(float seconds) implements LivingEffect {
  public static final MapCodec<IgniteEffect> CODEC =
      Codecs.POSITIVE_FLOAT.fieldOf("seconds").xmap(IgniteEffect::new, IgniteEffect::seconds);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    if (target.isFireImmune()) return;
    target.setOnFireFor(seconds);
  }

  @Override
  public MapCodec<IgniteEffect> getCodec() {
    return CODEC;
  }
}
