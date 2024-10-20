package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

public class SpawnProvider {
  private final Random random = Random.create();
  private ServerWorld world;
  private double difficulty;

  public SpawnProvider(ServerWorld world) {
    this.world = world;
  }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    if (pos.getY() < world.getBottomY()) return false;
    if (pos.getY()
        > world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()))
      return false;
    if (world.getBiome(pos).isIn(BiomeTags.WITHOUT_ZOMBIE_SIEGES)) return false;
    if (world.getLightLevel(LightType.BLOCK, pos) > 0) return false;
    if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16.0)) return false;
    if (!SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos)) return false;

    var spawnPos = pos.toBottomCenterPos();
    var boundingBox =
        EntityType.ZOMBIE.getSpawnBox(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  private Optional<BlockPos> giveRandomSpawnPos(BlockPos center, int range) {
    return Optional.of(RandomUtils.randomOffset(this.random, center, range))
        .filter(this::canSpawnAtPosBasic);
  }

  private Stream<BlockPos> giveExtraSpawnPositions(BlockPos initialPos) {
    var extraTries = ExtendedDifficulty.getExtraTries(difficulty);
    var maxSuccess = ExtendedDifficulty.getMaxExtraSuccessfulTries(difficulty);

    return IntStream.range(0, extraTries)
        .mapToObj(v -> giveRandomSpawnPos(initialPos, 16))
        .filter(Optional::isPresent)
        .limit(maxSuccess)
        .map(Optional::get);
  }

  public Stream<BlockPos> giveSpawnPositions(
      ServerWorld world, BlockPos centerPos, double difficulty) {
    this.world = world;
    this.difficulty = difficulty;
    return giveRandomSpawnPos(centerPos, 128).stream()
        .flatMap(v -> Stream.concat(Stream.of(v), giveExtraSpawnPositions(v)));
  }
}
