package org.codeberg.zenxarch.zombies.entity.variant;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public record SpawnContext(ServerWorld world,BlockPos pos) {
  
}
