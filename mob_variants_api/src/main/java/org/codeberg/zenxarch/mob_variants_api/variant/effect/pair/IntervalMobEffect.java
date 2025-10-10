package org.codeberg.zenxarch.mob_variants_api.variant.effect.pair;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record IntervalMobEffect(int ticks, MobEffect effect) implements MobEffect {

  public static final MapCodec<IntervalMobEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Codecs.POSITIVE_INT.fieldOf("ticks").forGetter(IntervalMobEffect::ticks),
                      MobEffect.CODEC.fieldOf("effect").forGetter(IntervalMobEffect::effect))
                  .apply(instance, IntervalMobEffect::new));

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (world.method_75260() % ticks == 0) effect.run(world, mob, adversery);
  }

  @Override
  public MapCodec<? extends MobEffect> getCodec() {
    return CODEC;
  }
}
