package org.codeberg.zenxarch.zombies.data.entity.effect.pair;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record ConditionalSpawnEffect(SpawnCondition condition, MobEffect effect, boolean forSelf)
    implements MobEffect {

  public static final MapCodec<ConditionalSpawnEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      SpawnCondition.CODEC
                          .fieldOf("condition")
                          .forGetter(ConditionalSpawnEffect::condition),
                      MobEffect.CODEC.fieldOf("effect").forGetter(ConditionalSpawnEffect::effect),
                      Codec.BOOL.fieldOf("forSelf").forGetter(ConditionalSpawnEffect::forSelf))
                  .apply(instance, ConditionalSpawnEffect::new));

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (!forSelf && adversery == null) return;
    var pos = forSelf ? mob.getBlockPos() : adversery.getBlockPos();
    if (condition.test(SpawnContext.of(world, pos))) effect.run(world, mob, adversery);
  }

  @Override
  public MapCodec<? extends MobEffect> getCodec() {
    return CODEC;
  }
}
