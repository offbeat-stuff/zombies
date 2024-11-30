package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect;
import org.jetbrains.annotations.Nullable;

public record SingleTargetZombieEffect(SingleLivingEffect effect, boolean targetZombie)
    implements ZombieEffect {

  public static final MapCodec<SingleTargetZombieEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      SingleLivingEffect.CODEC
                          .fieldOf("effect")
                          .forGetter(SingleTargetZombieEffect::effect),
                      Codec.BOOL
                          .fieldOf("targetZombie")
                          .forGetter(SingleTargetZombieEffect::targetZombie))
                  .apply(instance, SingleTargetZombieEffect::new));

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (targetZombie) {
      effect.run(world, zombie);
    } else if (adversery != null) {
      effect.run(world, adversery);
    }
  }

  @Override
  public MapCodec<SingleTargetZombieEffect> getCodec() {
    return null;
  }
}
