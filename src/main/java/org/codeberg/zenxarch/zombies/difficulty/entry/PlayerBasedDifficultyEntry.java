package org.codeberg.zenxarch.zombies.difficulty.entry;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;

public interface PlayerBasedDifficultyEntry extends DifficultyEntry {
  default double calculate(ServerWorld world, Chunk chunk) {
    double result = 0.0;
    int players = 0;
    for (var player : ZombieApocalypse.players(world))
      if (isWithinRange(player, chunk)) {
        result += calculate(world, player);
        players++;
      }
    if (players == 0) return 0.0;
    return result / players;
  }

  double calculate(ServerWorld world, ServerPlayerEntity player);

  private static boolean isWithinRange(ServerPlayerEntity player, Chunk chunk) {
    return chunk.getPos().getChebyshevDistance(player.getChunkPos()) < 8;
  }
}
