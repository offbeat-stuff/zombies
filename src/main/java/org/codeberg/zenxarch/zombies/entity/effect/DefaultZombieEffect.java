package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record DefaultZombieEffect() implements ZombieEffect {
  private static final DefaultZombieEffect INSTANCE = new DefaultZombieEffect();

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {}

  public static DefaultZombieEffect create() {
    return INSTANCE;
  }
}
