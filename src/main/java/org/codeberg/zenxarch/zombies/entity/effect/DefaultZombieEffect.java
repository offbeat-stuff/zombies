package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record DefaultZombieEffect() implements ZombieEffect {
  private static final DefaultZombieEffect INSTANCE = new DefaultZombieEffect();
  public static final MapCodec<DefaultZombieEffect> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {}

  @Override
  public MapCodec<DefaultZombieEffect> getCodec() {
    return CODEC;
  }

  public static DefaultZombieEffect create() {
    return INSTANCE;
  }
}
