package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record FreezeZombieEffect() implements ZombieEffect {
  private static final FreezeZombieEffect INSTANCE = new FreezeZombieEffect();

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    if (!adversery.canFreeze()) return;
    adversery.setFrozenTicks(adversery.getMinFreezeDamageTicks());
  }

  public static FreezeZombieEffect create() {
    return INSTANCE;
  }
}
