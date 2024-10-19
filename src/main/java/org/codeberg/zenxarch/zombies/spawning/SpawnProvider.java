package org.codeberg.zenxarch.zombies.spawning;

import static org.codeberg.zenxarch.zombies.Zombies.SPAWN_CONFIG;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.data.client.BlockStateVariantMap.QuadFunction;
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

  private static enum SpawnConditions {
    BIOME(SPAWN_CONFIG::skipSpawnIn),
    LIGHT(SPAWN_CONFIG::insufficientLightLevel),
    PLAYER_RANGE(SPAWN_CONFIG::isPlayerTooClose),
    POS_CHECK((w, b, r, d) -> !SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, w, b));

    private final QuadFunction<ServerWorld, BlockPos, Random, Double, Boolean> spawnCondition;

    private SpawnConditions(
        QuadFunction<ServerWorld, BlockPos, Random, Double, Boolean> spawnCondition) {
      this.spawnCondition = spawnCondition;
    }

    public boolean spawnCheck(ServerWorld world, BlockPos pos, Random random, Double difficulty) {
      return this.spawnCondition.apply(world, pos, random, difficulty);
    }

    public static boolean skipSpawn(
        ServerWorld world, BlockPos pos, Random random, Double difficulty, Debug debug) {
      for (var check : values()) {
        if (!check.spawnCheck(world, pos, random, difficulty)) continue;
        debug.spawnCheckNum(check.ordinal());
        return true;
      }
      return false;
    }
  }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    debug.spawnCheckNum(4);
    if (SpawnConditions.skipSpawn(this.world, pos, this.random, this.difficulty, this.debug))
      return false;

    this.debug.spawnCheck();

    var boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  private Optional<BlockPos> giveRandomPos(BlockPos center, int range) {
    int x = center.getX() + this.random.nextBetween(-range, range);
    int z = center.getZ() + this.random.nextBetween(-range, range);

    int minY = this.world.getBottomY();
    int maxY = this.world.getTopY(SpawnRestriction.getHeightmapType(EntityType.ZOMBIE), x, z) + 1;
    if (maxY < minY) return Optional.empty();

    var y = center.getY() + this.random.nextBetween(-range, range);
    if (y < minY || y > maxY) return Optional.empty();

    return Optional.of(new BlockPos(x, y, z));
  }

  private Optional<BlockPos> giveRandomSpawnPos(BlockPos center, int range) {
    return giveRandomPos(center, range).filter(this::canSpawnAtPosBasic);
  }

  private Stream<BlockPos> giveExtraSpawnPositions(BlockPos initialPos) {
    var extraRange = SPAWN_CONFIG.SPAWN_RANGE_FROM_INITIAL_POINT.value();

    var extraTries = ExtendedDifficulty.getExtraTries(difficulty);
    var maxSuccess = ExtendedDifficulty.getMaxExtraSuccessfulTries(difficulty);

    if (extraTries == 0 || maxSuccess == 0) return Stream.empty();

    return IntStream.range(0, extraTries)
        .mapToObj(v -> giveRandomSpawnPos(initialPos, extraRange))
        .filter(Optional::isPresent)
        .limit(maxSuccess)
        .map(Optional::get);
  }

  public Stream<BlockPos> giveSpawnPositions(
      ServerWorld world, BlockPos centerPos, double difficulty) {
    this.world = world;
    this.difficulty = difficulty;
    var baseRange = SPAWN_CONFIG.SPAWN_RANGE_FROM_PLAYER.value();
    return giveRandomSpawnPos(centerPos, baseRange).stream()
        .flatMap(v -> Stream.concat(Stream.of(v), giveExtraSpawnPositions(v)));
  }
}
