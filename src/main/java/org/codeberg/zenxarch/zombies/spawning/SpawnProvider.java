package org.codeberg.zenxarch.zombies.spawning;

import static org.codeberg.zenxarch.zombies.Zombies.SPAWN_CONFIG;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import org.codeberg.zenxarch.zombies.debug.Debug;

public class SpawnProvider {
  private final Random random = Random.create();
  private ServerWorld world;
  private final Debug debug;

  public SpawnProvider(ServerWorld world, Debug debug) {
    this.world = world;
    this.debug = debug;
  }

  private boolean cannotSpawnInBiomeAt(BlockPos pos) {
    var biome = world.getBiome(pos);
    return SPAWN_CONFIG.NO_SPAWN_IN_BIOMES.value().stream().anyMatch(b -> b.test(biome));
  }

  private boolean isLightLevelOk(BlockPos pos) {
    return SPAWN_CONFIG.BLOCKLIGHT.test(this.world, pos, this.world.random)
        && SPAWN_CONFIG.SKYLIGHT.test(this.world, pos, this.world.random);
  }

  private boolean isPlayerInRange(BlockPos pos) {
    return this.world.isPlayerInRange(
        pos.getX(), pos.getY(), pos.getZ(), SPAWN_CONFIG.NO_SPAWN_NEAR_PLAYER_RANGE.value());
  }

  private boolean canZombieSpawnAt(BlockPos pos) {
    return SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos);
  }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    if (!isLightLevelOk(pos)
        || isPlayerInRange(pos)
        || cannotSpawnInBiomeAt(pos)
        || !canZombieSpawnAt(pos)) {
      return false;
    }

    this.debug.spawnCheck();

    var boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  private Optional<BlockPos> giveRandomPos(BlockPos center) {
    var range = SPAWN_CONFIG.SPAWN_RANGE_FROM_INITIAL_POINT.value();
    int x = center.getX() + this.random.nextBetween(-range, range);
    int z = center.getZ() + this.random.nextBetween(-range, range);

    int minY = Math.max(this.world.getBottomY(), center.getY() - range);

    int maxY =
        Math.min(
            this.world.getTopY(SpawnRestriction.getHeightmapType(EntityType.ZOMBIE), x, z) + 1,
            center.getY() + range);

    if (maxY < minY) return Optional.empty();

    return Optional.of(new BlockPos(x, this.random.nextBetween(minY, maxY), z));
  }

  public Optional<BlockPos> giveSpawnPos(ServerWorld world, BlockPos centerPos, int times) {
    this.world = world;
    for (int i = 0; i < times; i++) {
      var pos = giveRandomPos(centerPos).filter(this::canSpawnAtPosBasic);
      if (pos.isPresent()) return pos;
    }
    return Optional.empty();
  }
}
