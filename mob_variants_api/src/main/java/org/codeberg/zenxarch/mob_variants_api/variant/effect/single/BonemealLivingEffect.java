package org.codeberg.zenxarch.zombies.data.entity.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;

public record BonemealLivingEffect() implements LivingEffect {

  public static final BonemealLivingEffect INSTANCE = new BonemealLivingEffect();
  public static final MapCodec<BonemealLivingEffect> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    useOnBlock(world, target.getBlockPos());
  }

  private boolean useOnBlock(ServerWorld world, BlockPos pos) {
    BlockState blockState = world.getBlockState(pos);
    if (!(blockState.getBlock() instanceof Fertilizable fertilizable)) return false;
    if (fertilizable.isFertilizable(world, pos, blockState)
        && fertilizable.canGrow(world, world.random, pos, blockState)) {
      fertilizable.grow((ServerWorld) world, world.random, pos, blockState);
      return true;
    }
    return false;
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
