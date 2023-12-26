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

  private static final Xoroshiro128PlusPlusRandom zombie_spawn_random =
      new Xoroshiro128PlusPlusRandom((new Random()).nextLong());
  private static final boolean Debug = false;

  public static void spawn_zombies_for_each_world(MinecraftServer server) {
    final int num_tries = 5;
    server.getWorlds().forEach(world -> {
      for (int i = 0; i < num_tries; i++) {
        var selected_player = world.getRandomAlivePlayer();
        if (selected_player == null) {
          continue;
        }
        spawn_zombies_around_given_pos(world, selected_player.getBlockPos());
      }
    });
  }

  private static void spawn_zombies_around_given_pos(ServerWorld world,
                                                     BlockPos pos) {
    final var spawn_pos = new_random_position_for_zombie_spawn().add(pos);

    spawn_zombie_at_given_pos(world, spawn_pos);
  }

  private static void spawn_zombie_at_given_pos(ServerWorld world,
                                                BlockPos pos) {
    var zombieOpt = ZombieFactory.BASE_ZOMBIE.getSpawnedZombie(world, pos);
    if (zombieOpt.isEmpty()) {
      return;
    }

    var zombie = zombieOpt.get();

    if (Debug) {
      zombie.addStatusEffect(
          new StatusEffectInstance(StatusEffects.GLOWING, -1));
      zombie.addStatusEffect(
          new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, -1));
      Zombies.LOGGER.info("Spawned zombie at: {}", pos);
    }
  }

  private static BlockPos new_random_position_for_zombie_spawn() {
    final int max_pos = 64;
    return new BlockPos(zombie_spawn_random.nextBetween(-max_pos, max_pos),
                        zombie_spawn_random.nextBetween(-max_pos, max_pos),
                        zombie_spawn_random.nextBetween(-max_pos, max_pos));
  }
}
