package org.codeberg.zenxarch.zombies.spawn;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public class SpawnConditions {
  public static boolean isBoxClear(ServerWorld world, Box boundingBox) {
    return world.isSpaceEmpty(boundingBox) && !world.containsFluid(boundingBox);
  }

  public static boolean doesEntityFit(ServerWorld world, Entity entity) {
    return world.doesNotIntersectEntities(entity) &&
        isBoxClear(world, entity.getBoundingBox());
  }

  public static boolean isWorldHostile(ServerWorld world) {
    return world.getDifficulty() != Difficulty.PEACEFUL &&
        world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING);
  }

  public static boolean isPlayerNearby(ServerWorld world, BlockPos pos,
                                       int distance) {
    return world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), distance);
  }

  public static boolean isInvalidSpawn(BlockState state, boolean isFireImmune) {
    if (!isFireImmune && LandPathNodeMaker.inflictsFireDamage(state)) {
      return false;
    }
    return state.isOf(Blocks.WITHER_ROSE) ||
        state.isOf(Blocks.SWEET_BERRY_BUSH) || state.isOf(Blocks.CACTUS) ||
        state.isOf(Blocks.POWDER_SNOW);
  }

  public static boolean isClearForSpawn(ServerWorld world, BlockPos pos,
                                        EntityType<?> entityType,
                                        boolean isFireImmune) {
    var state = world.getBlockState(pos);
    if (state.isFullCube(world, pos) || state.emitsRedstonePower() ||
        !world.getFluidState(pos).isEmpty() ||
        state.isIn(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
      return false;
    }
    return !isInvalidSpawn(state, isFireImmune);
  }

  public static boolean canSpawn(ServerWorld world, BlockPos pos,
                                 @Nullable EntityType<?> entityType,
                                 Box boundingBox, boolean isFireImmune) {
    if (!world.getWorldBorder().contains(boundingBox)) {
      return false;
    }

    BlockPos blockPos = pos.up();
    BlockPos blockPos2 = pos.down();
    BlockState blockState2 = world.getBlockState(blockPos2);

    if (!blockState2.allowsSpawning(world, blockPos2, entityType)) {
      return false;
    }

    return isClearForSpawn(world, pos, entityType, isFireImmune) &&
        isClearForSpawn(world, blockPos, entityType, isFireImmune);
  }
}