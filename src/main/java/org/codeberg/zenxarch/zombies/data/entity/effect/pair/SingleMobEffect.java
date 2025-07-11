package org.codeberg.zenxarch.zombies.data.entity.effect.pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record SingleMobEffect(LivingEffect effect, boolean forSelf) implements MobEffect {

  public static final MapCodec<SingleMobEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      LivingEffect.CODEC.fieldOf("effect").forGetter(SingleMobEffect::effect),
                      Codec.BOOL
                          .optionalFieldOf("forSelf", true)
                          .forGetter(SingleMobEffect::forSelf))
                  .apply(instance, SingleMobEffect::new));

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (forSelf) {
      effect.run(world, mob);
    } else if (adversery != null) {
      effect.run(world, adversery);
    }
  }

  @Override
  public MapCodec<SingleMobEffect> getCodec() {
    return CODEC;
  }
}
