package org.codeberg.zenxarch.zombies.entity.effect.single;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public interface SingleLivingEffect {
  public void run(ServerWorld world, LivingEntity target);
}
