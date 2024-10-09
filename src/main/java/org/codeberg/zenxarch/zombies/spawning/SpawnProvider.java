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
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public class SpawnProvider {
  private final Random random = Random.create();
  private ServerWorld world;
  private double difficulty;
  private final Debug debug;

  public SpawnProvider(ServerWorld world, Debug debug) {
    this.world = world;
    this.debug = debug;
  }

  private boolean cannotSpawnInBiomeAt(BlockPos pos) {
    var biome = world.getBiome(pos);
    var result = SPAWN_CONFIG.skipSpawnIn(biome, this.random, this.difficulty);
    if (result) debug.spawnCheckNum(0);
    return result;
  }

  private boolean isLightLevelBad(BlockPos pos) {
    var result =
        !(SPAWN_CONFIG.BLOCKLIGHT.test(this.world, pos, this.world.random)
            && SPAWN_CONFIG.SKYLIGHT.test(this.world, pos, this.world.random));
    if (result) debug.spawnCheckNum(1);
    return result;
  }

  private boolean isPlayerInRange(BlockPos pos) {
    var result =
        this.world.isPlayerInRange(
            pos.getX(), pos.getY(), pos.getZ(), SPAWN_CONFIG.NO_SPAWN_NEAR_PLAYER_RANGE.value());
    if (result) debug.spawnCheckNum(2);
    return result;
  }

  private boolean zombieCannotSpawnAt(BlockPos pos) {
    var result = !SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos);
    if (result) debug.spawnCheckNum(3);
    return result;
  }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    debug.spawnCheckNum(4);
    if (isLightLevelBad(pos)
        || isPlayerInRange(pos)
        || cannotSpawnInBiomeAt(pos)
        || zombieCannotSpawnAt(pos)) {
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

    int minY = this.world.getBottomY();
    int maxY = this.world.getTopY(SpawnRestriction.getHeightmapType(EntityType.ZOMBIE), x, z) + 1;
    if (maxY < minY) return Optional.empty();

    var y = center.getY() + this.random.nextBetween(-range, range);
    if (y < minY || y > maxY) return Optional.empty();

    return Optional.of(new BlockPos(x, y, z));
  }

  public Optional<BlockPos> giveSpawnPos(ServerWorld world, BlockPos centerPos, double difficulty) {
    var times = ExtendedDifficulty.getTriesForSpawning(difficulty);
    this.world = world;
    this.difficulty = difficulty;
    for (int i = 0; i < times; i++) {
      var pos = giveRandomPos(centerPos).filter(this::canSpawnAtPosBasic);
      if (pos.isPresent()) return pos;
    }
    return Optional.empty();
  }
}
