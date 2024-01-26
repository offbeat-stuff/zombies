package org.codeberg.zenxarch.zombies.spawning;

import static org.codeberg.zenxarch.zombies.Zombies.LOGGER;

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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.spawner.SpecialSpawner;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedZombieEntity;

public class ZombieApocalypse implements SpecialSpawner {

  private final Random random = Random.create();
  private ServerWorld world;

  public ZombieApocalypse(ServerWorld world) { this.world = world; }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    if (this.world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16)) {
      return false;
    }

    if (this.world.getDifficulty().equals(Difficulty.PEACEFUL) ||
        !this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
      return false;
    }

    var entityType = EntityType.ZOMBIE;
    var location = SpawnRestriction.getLocation(entityType);

    return SpawnHelper.canSpawn(location, this.world, pos, entityType) &&
        MobEntity.canMobSpawn(EntityType.ZOMBIE, this.world,
                              SpawnReason.NATURAL, pos, this.world.random);
  }

  private boolean canSpawnAtPosSpace(ZombieEntity zombie) {
    return this.world.doesNotIntersectEntities(zombie) &&
        this.world.isSpaceEmpty(zombie) &&
        !this.world.containsFluid(zombie.getBoundingBox());
  }

  private Optional<BlockPos> findNearestWorking(BlockPos pos, int times) {
    return IntStream.range(0, times)
        .mapToObj(i
                  -> pos.add(random.nextBetween(-64, 64),
                             random.nextBetween(-64, 64),
                             random.nextBetween(-64, 64)))
        .filter(this::canSpawnAtPosBasic)
        .findFirst();
  }

  public boolean spawnZombieAt(BlockPos ppos) {
    var difficulty = new ExtendedDifficulty(this.world, ppos);
    var zombieOpt = findNearestWorking(ppos, difficulty.getTriesForSpawning())
                        .map(u -> new ExtendedZombieEntity(this.world, u))
                        .filter(this::canSpawnAtPosSpace);

    zombieOpt.ifPresent(z -> {
      z.initialize(this.world);
      this.world.spawnEntityAndPassengers(z);
    });

    return zombieOpt.isPresent();
  }

  public boolean isSuitablePlayer(ServerPlayerEntity player) {
    return player.isAlive();
  }

  @Override
  public int spawn(ServerWorld world, boolean spawnMonsters,
                   boolean spawnAnimals) {
    this.world = world;
    if (!spawnMonsters) {
      return 0;
    }
    var result = 0;
    for (var player : this.world.getPlayers(this::isSuitablePlayer)) {
      result += this.spawnZombieAt(player.getBlockPos()) ? 1 : 0;
    }
    if (result > 0) {
      LOGGER.info("Spawner {} zombies", result);
    }
    return result;
  }

  public static boolean isApocalypticWorld(ServerWorld world) {
    if (world.getRegistryKey().equals(World.OVERWORLD)) {
      return true;
    }
    return false;
  }
}
