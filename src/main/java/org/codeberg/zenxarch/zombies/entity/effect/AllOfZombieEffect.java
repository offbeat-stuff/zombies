package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record AllOfZombieEffect(List<ZombieEffect> effects) implements ZombieEffect {

  public static final MapCodec<AllOfZombieEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      ZombieEffect.CODEC
                          .listOf()
                          .fieldOf("effects")
                          .forGetter(AllOfZombieEffect::effects))
                  .apply(instance, AllOfZombieEffect::new));

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    for (ZombieEffect effect : effects) effect.run(world, zombie, adversery);
  }

  @Override
  public MapCodec<AllOfZombieEffect> getCodec() {
    return CODEC;
  }

  public static ZombieEffect create(ZombieEffect... effects) {
    if (effects.length == 0) return DefaultZombieEffect.create();
    if (effects.length == 1) return effects[0];
    return new AllOfZombieEffect(List.of(effects));
  }
}
