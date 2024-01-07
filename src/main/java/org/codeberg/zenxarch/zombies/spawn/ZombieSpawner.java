package org.codeberg.zenxarch.zombies.spawn;

import java.util.Random;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Xoroshiro128PlusPlusRandom;
import org.codeberg.zenxarch.zombies.Zombies;

public class ZombieSpawner {

  private static final Xoroshiro128PlusPlusRandom ZOMBIE_SPAWN_RANDOM =
      new Xoroshiro128PlusPlusRandom((new Random()).nextLong());
  private static final boolean DEBUG_MODE = false;
  private static final int NUM_TRIES = 5;
  private static final int MAX_POS = 64;

  public static void spawnZombiesForEachWorld(MinecraftServer server) {
    server.getWorlds().forEach(world -> {
      for (int i = 0; i < NUM_TRIES; i++) {
        var selectedPlayer = world.getRandomAlivePlayer();
        if (selectedPlayer == null) {
          break;
        }
        spawnZombiesAroundGivenPos(world, selectedPlayer.getBlockPos());
      }
    });
  }

  private static void spawnZombiesAroundGivenPos(ServerWorld world,
                                                 BlockPos pos) {
    var spawnPos = getRandomPositionForZombieSpawn().add(pos);
    spawnZombieAtGivenPos(world, spawnPos);
  }

  private static void spawnZombieAtGivenPos(ServerWorld world, BlockPos pos) {
    var zombieOpt = ZombieFactory.BASE_ZOMBIE.getSpawnedZombie(world, pos);
    if (zombieOpt.isEmpty()) {
      return;
    }

    var zombie = zombieOpt.get();

    if (DEBUG_MODE) {
      zombie.addStatusEffect(
          new StatusEffectInstance(StatusEffects.GLOWING, -1));
      zombie.addStatusEffect(
          new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, -1));
      Zombies.LOGGER.info("Spawned zombie at: {}", pos);
    }
  }

  private static BlockPos getRandomPositionForZombieSpawn() {
    return new BlockPos(ZOMBIE_SPAWN_RANDOM.nextBetween(-MAX_POS, MAX_POS),
                        ZOMBIE_SPAWN_RANDOM.nextBetween(-MAX_POS, MAX_POS),
                        ZOMBIE_SPAWN_RANDOM.nextBetween(-MAX_POS, MAX_POS));
  }
}
