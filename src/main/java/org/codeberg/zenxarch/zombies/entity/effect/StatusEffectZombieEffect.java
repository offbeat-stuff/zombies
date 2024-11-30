package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.effect.util.StatusEffectInstanceBuilder;
import org.jetbrains.annotations.Nullable;

public record StatusEffectZombieEffect(List<StatusEffectInstanceBuilder> effects)
    implements ZombieEffect {

  public static final MapCodec<StatusEffectZombieEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      StatusEffectInstanceBuilder.CODEC
                          .listOf(1, Integer.MAX_VALUE)
                          .fieldOf("effects")
                          .forGetter(StatusEffectZombieEffect::effects))
                  .apply(instance, StatusEffectZombieEffect::new));

  public StatusEffectZombieEffect(StatusEffectInstanceBuilder effect) {
    this(List.of(effect));
  }

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    var random = zombie.getRandom();
    var optionalEntry = Util.getRandomOrEmpty(effects, random);
    if (optionalEntry.isPresent()) adversery.addStatusEffect(optionalEntry.get().build(), zombie);
  }

  @Override
  public MapCodec<StatusEffectZombieEffect> getCodec() {
    return CODEC;
  }
}
