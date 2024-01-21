package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;

public class ExtendedDifficulty {
  public static double calculateDifficulty(ServerWorld world, BlockPos pos) {
    var inhibitedMinutes = 0.0;
    var moonSize = 0.0;
    if (world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()),
                            ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = world.getMoonSize();
      inhibitedMinutes =
          (double)world.getWorldChunk(pos).getInhabitedTime() / (60 * 20);
    }

    var difficulty = (double)world.getDifficulty().getId();
    var days = (double)world.getTimeOfDay() / 24000.0;

    var daysFactor = MathHelper.clamp(days / 100.0, 0.0, 1.0);
    var inhibitedMinutesFactor =
        MathHelper.clamp(inhibitedMinutes / (60.0 * 100.0), 0.0, 1.0);

    var diff = difficulty * ((0.125 * moonSize) + (0.25 * daysFactor) +
                             (0.125 * inhibitedMinutesFactor));

    return MathHelper.clamp(diff, 0.0, 1.0);
  }
}
