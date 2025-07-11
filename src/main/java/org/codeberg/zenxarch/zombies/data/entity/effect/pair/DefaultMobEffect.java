package org.codeberg.zenxarch.zombies.data.entity.effect.pair;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record DefaultMobEffect() implements MobEffect {
  private static final DefaultMobEffect INSTANCE = new DefaultMobEffect();
  public static final MapCodec<DefaultMobEffect> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    /* No op */
  }

  @Override
  public MapCodec<DefaultMobEffect> getCodec() {
    return CODEC;
  }

  public static DefaultMobEffect create() {
    return INSTANCE;
  }
}
