package org.codeberg.zenxarch.zombies.entity.effect;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record AllOfZombieEffect(List<ZombieEffect> effects) implements ZombieEffect {
  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    for (ZombieEffect effect : effects) effect.run(world, zombie, adversery);
  }

  public static ZombieEffect create(ZombieEffect... effects) {
    if (effects.length == 0) return DefaultZombieEffect.create();
    if (effects.length == 1) return effects[0];
    return new AllOfZombieEffect(List.of(effects));
  }
}
