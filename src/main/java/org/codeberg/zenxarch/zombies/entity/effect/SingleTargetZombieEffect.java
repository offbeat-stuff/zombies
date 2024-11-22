package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect;
import org.jetbrains.annotations.Nullable;

public record SingleTargetZombieEffect(SingleLivingEffect effect, boolean targetZombie)
    implements ZombieEffect {

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (targetZombie) {
      effect.run(world, zombie);
    } else if (adversery != null) {
      effect.run(world, adversery);
    }
  }
}
