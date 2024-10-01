package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import org.codeberg.zenxarch.zombies.debug.Debug;

public class SpawnProvider {
  private final Random random = Random.create();
  private ServerWorld world;
  private final Debug debug;

  public SpawnProvider(ServerWorld world, Debug debug) {
    this.world = world;
    this.debug = debug;
  }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    if (this.world.getLightLevel(LightType.BLOCK, pos) > 0
        || this.world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16)
        || this.world.getBiome(pos).isIn(BiomeTags.WITHOUT_ZOMBIE_SIEGES)
        || !SpawnRestriction.canSpawn(
            EntityType.ZOMBIE, this.world, SpawnReason.NATURAL, pos, this.world.random)
        ||
        // !SpawnHelper.canSpawn(SpawnRestriction.getLocation(EntityType.ZOMBIE),
        // this.world, pos, EntityType.ZOMBIE) ||
        !MobEntity.canMobSpawn(
            EntityType.ZOMBIE, this.world, SpawnReason.NATURAL, pos, this.world.random)) {
      return false;
    };

    this.debug.spawnCheck();

    var boundingBox = EntityType.ZOMBIE.getSpawnBox(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

    return world.doesNotIntersectEntities(null, VoxelShapes.cuboid(boundingBox))
        && world.isSpaceEmpty(boundingBox) && !world.containsFluid(boundingBox);
  }

  private static int SPAWN_RANGE = 64;

  private BlockPos giveRandomPos(BlockPos center) {
    int x = this.random.nextBetween(-SPAWN_RANGE, SPAWN_RANGE);
    int z = this.random.nextBetween(-SPAWN_RANGE, SPAWN_RANGE);

    int minY = Math.max(this.world.getBottomY(), center.getY() - SPAWN_RANGE);

    int maxY = Math.min(this.world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z),
        center.getY() + SPAWN_RANGE);

    return center.add(x, this.random.nextBetween(minY, maxY), z);
  }

  public Optional<BlockPos> giveSpawnPos(ServerWorld world, BlockPos centerPos, int times) {
    this.world = world;
    for (int i = 0; i < times; i++) {
      var pos = giveRandomPos(centerPos);
      if (canSpawnAtPosBasic(pos)) {
        return Optional.of(pos);
      }
    }
    return Optional.empty();
  }
}
