package org.codeberg.zenxarch.zombies.spawning;

import static org.codeberg.zenxarch.zombies.Zombies.zrx;

import java.util.Optional;
import java.util.stream.IntStream;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.difficulty.NewZombie;

public class Spawning {
  private static boolean canSpawnAtPosBasic(ServerWorld world, BlockPos pos) {
    if (world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16)) {
      return false;
    }

    if (world.getDifficulty().equals(Difficulty.PEACEFUL) ||
        !world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
      return false;
    }

    var entityType = EntityType.ZOMBIE;
    var location = SpawnRestriction.getLocation(entityType);

    return SpawnHelper.canSpawn(location, world, pos, entityType) &&
        MobEntity.canMobSpawn(EntityType.ZOMBIE, world, SpawnReason.NATURAL,
                              pos, world.random);
  }

  private static boolean canSpawnAtPosSpace(ServerWorld world,
                                            ZombieEntity zombie) {
    return world.doesNotIntersectEntities(zombie) &&
        world.isSpaceEmpty(zombie) &&
        !world.containsFluid(zombie.getBoundingBox());
  }

  private static Optional<BlockPos> findNearestWorking(ServerWorld world,
                                                       BlockPos pos) {
    return IntStream
        .range(0, 2 + (int)(ExtendedDifficulty.calculateDifficulty(world, pos) *
                            23.0))
        .mapToObj(i
                  -> pos.add(zrx.nextBetween(-64, 64), zrx.nextBetween(-64, 64),
                             zrx.nextBetween(-64, 64)))
        .filter(p -> canSpawnAtPosBasic(world, p))
        .findFirst();
  }

  public static boolean spawnZombieAt(ServerWorld world, BlockPos bpos) {

    var zombieOpt = findNearestWorking(world, bpos)
                        .map(u -> NewZombie.make(world, u))
                        .filter(z -> canSpawnAtPosSpace(world, z));

    zombieOpt.ifPresent(z -> {
      NewZombie.initialize(world, z);
      world.spawnEntityAndPassengers(z);
    });

    return zombieOpt.isPresent();
  }

  public static boolean isSuitablePlayer(ServerPlayerEntity player) {
    return player.isAlive();
  }
}
