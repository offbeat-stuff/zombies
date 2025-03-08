package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record ConditionalSpawnEffect(
    SpawnCondition condition, ZombieEffect effect, boolean testZombie) implements ZombieEffect {

  public static final MapCodec<ConditionalSpawnEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      SpawnCondition.CODEC
                          .fieldOf("condition")
                          .forGetter(ConditionalSpawnEffect::condition),
                      ZombieEffect.CODEC
                          .fieldOf("effect")
                          .forGetter(ConditionalSpawnEffect::effect),
                      Codec.BOOL
                          .fieldOf("testZombie")
                          .forGetter(ConditionalSpawnEffect::testZombie))
                  .apply(instance, ConditionalSpawnEffect::new));

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (!testZombie && adversery == null) return;
    var pos = testZombie ? zombie.getBlockPos() : adversery.getBlockPos();
    if (condition.test(SpawnContext.of(world, pos))) effect.run(world, zombie, adversery);
  }

  @Override
  public MapCodec<? extends ZombieEffect> getCodec() {
    return CODEC;
  }
}
