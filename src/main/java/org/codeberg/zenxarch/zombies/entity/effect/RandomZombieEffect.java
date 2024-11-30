package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record RandomZombieEffect(List<ZombieEffect> effects) implements ZombieEffect {
  public static final MapCodec<RandomZombieEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      ZombieEffect.CODEC
                          .listOf()
                          .fieldOf("effects")
                          .forGetter(RandomZombieEffect::effects))
                  .apply(instance, RandomZombieEffect::new));

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    Util.getRandomOrEmpty(effects, zombie.getRandom())
        .ifPresent(effect -> effect.run(world, zombie, adversery));
  }

  @Override
  public MapCodec<RandomZombieEffect> getCodec() {
    return null;
  }

  public static ZombieEffect create(ZombieEffect... effects) {
    if (effects.length == 0) return DefaultZombieEffect.create();
    if (effects.length == 1) return effects[0];
    return new RandomZombieEffect(List.of(effects));
  }
}
