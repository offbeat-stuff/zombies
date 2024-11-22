package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record SwapPositionZombieEffect() implements ZombieEffect {
  private static final SwapPositionZombieEffect INSTANCE = new SwapPositionZombieEffect();

  private static TeleportTarget getTeleportTarget(ServerWorld world, LivingEntity living) {
    return new TeleportTarget(
        world,
        living.getPos(),
        living.getVelocity(),
        living.getYaw(),
        living.getPitch(),
        TeleportTarget.NO_OP);
  }

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (adversery == null) return;

    var toAdversary = getTeleportTarget(world, adversery);
    var toZombie = getTeleportTarget(world, zombie);

    zombie.teleportTo(toAdversary);
    adversery.teleportTo(toZombie);
  }

  public static SwapPositionZombieEffect create() {
    return INSTANCE;
  }
}
