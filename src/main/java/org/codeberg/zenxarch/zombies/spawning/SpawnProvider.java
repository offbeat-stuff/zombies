package org.codeberg.zenxarch.zombies.spawning;

import static org.codeberg.zenxarch.zombies.Zombies.CONFIG;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.debug.Debug;

public class SpawnProvider {
  private final Random random = Random.create();
  private ServerWorld world;
  private final Debug debug;

  public SpawnProvider(ServerWorld world, Debug debug) {
    this.world = world;
    this.debug = debug;
  }

  private boolean cannotSpawnInBiome(RegistryEntry<Biome> biome) {
    return CONFIG.NO_SPAWN_IN_BIOMES.value().stream().anyMatch(b -> b.test(biome));
  }

  private boolean isLightLevelOk(BlockPos pos) {
    return CONFIG.BLOCKLIGHT.test(this.world, pos, this.world.random)
        && CONFIG.SKYLIGHT.test(this.world, pos, this.world.random);
  }

  // Using MobEntity.canMobSpawn instead of SpawnRestriction.canSpawn
  // to remove check for light level/difficulty
  private boolean canSpawnAtPosBasic(BlockPos pos) {
    if (!isLightLevelOk(pos)
        || this.world.isPlayerInRange(
            pos.getX(), pos.getY(), pos.getZ(), CONFIG.NO_SPAWN_NEAR_PLAYER_RANGE.value())
        || cannotSpawnInBiome(this.world.getBiome(pos))
        || !SpawnRestriction.isSpawnPosAllowed(EntityType.ZOMBIE, world, pos)
        || !MobEntity.canMobSpawn(
            EntityType.ZOMBIE, this.world, SpawnReason.NATURAL, pos, this.world.random)) {
      return false;
    }

    this.debug.spawnCheck();

    var boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

    return !world.containsFluid(boundingBox)
        && world.isSpaceEmpty(boundingBox)
        && world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox));
  }

  private Optional<BlockPos> giveRandomPos(BlockPos center) {
    var range = CONFIG.SPAWN_RANGE_FROM_INITIAL_POINT.value();
    int x = center.getX() + this.random.nextBetween(-range, range);
    int z = center.getZ() + this.random.nextBetween(-range, range);

    int minY = Math.max(this.world.getBottomY(), center.getY() - range);

    int maxY =
        Math.min(
            this.world.getTopY(SpawnRestriction.getHeightmapType(EntityType.ZOMBIE), x, z),
            center.getY() + range);

    if (maxY < minY) return Optional.empty();

    return Optional.of(new BlockPos(x, this.random.nextBetween(minY, maxY), z));
  }

  public Optional<BlockPos> giveSpawnPos(ServerWorld world, BlockPos centerPos, int times) {
    this.world = world;
    for (int i = 0; i < times; i++) {
      var pos = giveRandomPos(centerPos);
      if (pos.isEmpty()) continue;
      if (canSpawnAtPosBasic(pos.get())) {
        return pos;
      }
    }
    return Optional.empty();
  }
}
