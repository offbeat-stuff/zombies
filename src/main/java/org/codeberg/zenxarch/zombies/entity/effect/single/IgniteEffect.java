package org.codeberg.zenxarch.zombies.entity.effect.single;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public record IgniteEffect(float seconds) implements SingleLivingEffect {
  @Override
  public void run(ServerWorld world, LivingEntity target) {
    if (target.isFireImmune()) return;
    target.setOnFireFor(seconds);
  }
}
