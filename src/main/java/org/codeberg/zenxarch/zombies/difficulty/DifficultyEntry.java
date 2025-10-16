package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.Chunk;

public interface DifficultyEntry {
  int getWeight();

  double calculate(ServerWorld world, Chunk chunk);
}
