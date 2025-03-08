package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record RandomZombieEffect(List<ZombieEffect> effects, FloatProvider chance)
    implements ZombieEffect {

  public static final MapCodec<RandomZombieEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Codecs.listOrSingle(ZombieEffect.CODEC)
                          .fieldOf("effects")
                          .forGetter(RandomZombieEffect::effects),
                      FloatProvider.VALUE_CODEC
                          .optionalFieldOf("chance", ConstantFloatProvider.create(1.0f))
                          .forGetter(RandomZombieEffect::chance))
                  .apply(instance, RandomZombieEffect::new));

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    var random = zombie.getRandom();
    if (random.nextDouble() >= chance.get(random)) return;
    Util.getRandomOrEmpty(effects, random)
        .ifPresent(effect -> effect.run(world, zombie, adversery));
  }

  @Override
  public MapCodec<RandomZombieEffect> getCodec() {
    return CODEC;
  }

  public static ZombieEffect create(ZombieEffect... effects) {
    if (effects.length == 0) return DefaultZombieEffect.create();
    if (effects.length == 1) return effects[0];
    return new RandomZombieEffect(List.of(effects), ConstantFloatProvider.create(1.0f));
  }

  public static ZombieEffect create(FloatProvider chance, ZombieEffect... effects) {
    if (effects.length == 0) return DefaultZombieEffect.create();
    return new RandomZombieEffect(List.of(effects), chance);
  }
}
