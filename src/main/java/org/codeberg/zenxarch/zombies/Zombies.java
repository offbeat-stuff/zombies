package org.codeberg.zenxarch.zombies;

import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.SpawnHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER =
      LoggerFactory.getLogger("zombies_zenxarch");

  public static final Xoroshiro128PlusPlusRandom zrx =
      new Xoroshiro128PlusPlusRandom((new Random()).nextLong());

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
  }

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
                              pos, zrx);
  }

  private static boolean canSpawnAtPosSpace(ServerWorld world,
                                            ZombieEntity zombie) {
    return world.doesNotIntersectEntities(zombie) &&
        world.isSpaceEmpty(zombie) &&
        !world.containsFluid(zombie.getBoundingBox());
  }

  private static Optional<BlockPos> findNearestWorking(ServerWorld world,
                                                       BlockPos pos) {
    for (int i = 0; i < 10; i++) {
      var posx = pos.add(zrx.nextBetween(-64, 64), zrx.nextBetween(-64, 64),
                         zrx.nextBetween(-64, 64));
      if (canSpawnAtPosBasic(world, posx)) {
        return Optional.of(posx);
      }
    }

    return Optional.empty();
  }

  private static boolean spawnZombieAt(ServerWorld world, BlockPos bpos) {
    var posOpt = findNearestWorking(world, bpos);

    if (posOpt.isEmpty()) {
      return false;
    }

    var pos = posOpt.get();

    var zombie = EntityType.ZOMBIE.create(world);
    zombie.setPosition(pos.getX(), pos.getY(), pos.getZ());

    if (!canSpawnAtPosSpace(world, zombie)) {
      return false;
    }

    zombie.initialize(world, world.getLocalDifficulty(pos), SpawnReason.NATURAL,
                      null, null);
    world.spawnEntityAndPassengers(zombie);
    return true;
  }

  private static boolean isSuitablePlayer(ServerPlayerEntity player) {
    return player.isAlive();
  }

  public static void spawnZombiesForEachWorld(MinecraftServer server) {
    var world = server.getOverworld();

    for (var player : world.getPlayers(Zombies::isSuitablePlayer)) {
      spawnZombieAt(world, player.getBlockPos());
    }
  }
}
