package org.codeberg.zenxarch.zombies.spawn;

import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;

public class ZombieFactory {

  public record ZombiePredicateEntry(EntityType<?> entityType,
                                     Predicate<ServerWorld> dimensionCheck,
                                     Predicate<PositionDetails> posCheck) {
    public static ZombiePredicateEntry default_entry = new ZombiePredicateEntry(
        EntityType.ZOMBIE,
        world
        -> world.getRegistryKey().equals(World.OVERWORLD),
        positionDetails
        -> SpawnHelper.canSpawn(SpawnRestriction.getLocation(EntityType.ZOMBIE),
                                positionDetails.world, positionDetails.pos,
                                EntityType.ZOMBIE));

    public ZombiePredicateEntry withEntityType(EntityType<?> entityType1) {
      return new ZombiePredicateEntry(entityType1, dimensionCheck, posCheck);
    }

    public ZombiePredicateEntry withDimensionCheck(
        Predicate<ServerWorld> dimensionCheck1) {
      return new ZombiePredicateEntry(entityType, dimensionCheck1, posCheck);
    }

    public ZombiePredicateEntry withPosCheck(
        Predicate<PositionDetails> posCheck1) {
      return new ZombiePredicateEntry(entityType, dimensionCheck, posCheck1);
    }
  }

  public record ZombieAbilitesEntry(boolean fireImmunity,
                                    boolean daylightImmunity, int strength) {
    public static ZombieAbilitesEntry default_entry =
        new ZombieAbilitesEntry(false, false, 1);
  }

  public record PositionDetails(ServerWorld world, BlockPos feetPos,
                                EntityType<?> entityType,
                                PositionDetailsData data) {

    private PositionDetails(ServerWorld world, BlockPos feetPos,
                            EntityType<?> entityType) {
      this.world = world;
      this.feetPos = feetPos;
      this.entityType = entityType;

      this.data = new PositionDetailsData(
          world.getLightLevel(LightType.BLOCK, this.feetPos),
          world.getLightLevel(LightType.SKY, this.feetPos),
          world.getFluidState(this.feetPos),
          world.getFluidState(this.feetPos.down()),
          world.getFluidState(this.feetPos.up()), world.getBlockState(feetPos),
          world.getBlockState(feetPos.down()),
          world.getBlockState(feetPos.up()));
    }

    public static PositionDetails createPositionDetails(
        ServerWorld world, BlockPos feetPos, EntityType<?> entityType) {
      return new PositionDetails(world, feetPos, entityType);
    }
  }

  public record PositionDetailsData(int blocklight, int skylight,
                                    FluidState fluidAt, FluidState fluidBelow,
                                    FluidState fluidAbove, BlockState blockAt,
                                    BlockState blockBelow,
                                    BlockState blockAbove) {}
}
