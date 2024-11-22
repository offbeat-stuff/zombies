package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public interface ZombieEffect {
  public void run(ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery);
}
