package org.codeberg.zenxarch.zombies.data.entity.effect.pair;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record SwapMobEffect() implements MobEffect {
  private static final SwapMobEffect INSTANCE = new SwapMobEffect();
  public static final MapCodec<SwapMobEffect> CODEC = MapCodec.unit(INSTANCE);

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
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (adversery == null) return;

    var toAdversary = getTeleportTarget(world, adversery);
    var toSelf = getTeleportTarget(world, mob);

    mob.teleportTo(toAdversary);
    adversery.teleportTo(toSelf);
  }

  @Override
  public MapCodec<SwapMobEffect> getCodec() {
    return CODEC;
  }

  public static SwapMobEffect create() {
    return INSTANCE;
  }
}
