package org.codeberg.zenxarch.zombies.data.entity.effect.pair;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record RandomMobEffect(List<MobEffect> effects, FloatProvider chance) implements MobEffect {

  public static final MapCodec<RandomMobEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Codecs.listOrSingle(MobEffect.CODEC)
                          .fieldOf("effects")
                          .forGetter(RandomMobEffect::effects),
                      FloatProvider.VALUE_CODEC
                          .optionalFieldOf("chance", ConstantFloatProvider.create(1.0f))
                          .forGetter(RandomMobEffect::chance))
                  .apply(instance, RandomMobEffect::new));

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    var random = mob.getRandom();
    if (random.nextDouble() >= chance.get(random)) return;
    Util.getRandomOrEmpty(effects, random).ifPresent(effect -> effect.run(world, mob, adversery));
  }

  @Override
  public MapCodec<RandomMobEffect> getCodec() {
    return CODEC;
  }

  public static MobEffect create(MobEffect... effects) {
    if (effects.length == 0) return DefaultMobEffect.create();
    if (effects.length == 1) return effects[0];
    return new RandomMobEffect(List.of(effects), ConstantFloatProvider.create(1.0f));
  }

  public static MobEffect create(FloatProvider chance, MobEffect... effects) {
    if (effects.length == 0) return DefaultMobEffect.create();
    return new RandomMobEffect(List.of(effects), chance);
  }
}
