package org.codeberg.zenxarch.zombies.data.entity.effect.single;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.World.ExplosionSourceType;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;

public record ExplosionEffect(float power) implements LivingEffect {

  public static final MapCodec<ExplosionEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(Codecs.POSITIVE_FLOAT.fieldOf("power").forGetter(ExplosionEffect::power))
                  .apply(instance, ExplosionEffect::new));

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    world.createExplosion(
        target, target.getX(), target.getY(), target.getZ(), this.power, ExplosionSourceType.NONE);
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
