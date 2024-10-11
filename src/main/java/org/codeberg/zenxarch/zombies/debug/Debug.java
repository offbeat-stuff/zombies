package org.codeberg.zenxarch.zombies.debug;

import static org.codeberg.zenxarch.zombies.Zombies.DEBUG_CONFIG;
import static org.codeberg.zenxarch.zombies.Zombies.LOGGER;

import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficultyInfo;

public class Debug {
  private int spawnAttempts = 0;
  private int successfulSpawnAttempts = 0;

  private int spawnPosChecks = 0;
  private int[] spawnCheckFails = new int[5];

  public Debug() {}

  public void spawnCheck() {
    spawnPosChecks++;
  }

  public void spawnCheckNum(int index) {
    spawnCheckFails[index]++;
  }

  private void sendPlayerDebugInfo(ServerWorld world) {
    for (var player : world.getPlayers(LivingEntity::isAlive)) {
      var difficulty = ExtendedDifficultyInfo.getDifficulty(world, player.getBlockPos());
      var text = Text.of(String.format("Difficulty: %.3f", difficulty));
      player.networkHandler.sendPacket(new OverlayMessageS2CPacket(text));
    }
  }

  public void attemptedSpawn(ServerWorld world, boolean successful) {
    spawnAttempts++;
    successfulSpawnAttempts += successful ? 1 : 0;
    if (DEBUG_CONFIG.SEND_PLAYER_DEBUG_INFO.value()) sendPlayerDebugInfo(world);
    if (!DEBUG_CONFIG.LOGGING.value()) return;
    if (spawnAttempts % (20 * DEBUG_CONFIG.LOGGING_INTERVAL.value()) == 0) {
      LOGGER.info(
          "Successful Attemps : {} , Total Attempts: {},spawnChecks : {},spawnCheckFails : {}",
          successfulSpawnAttempts,
          spawnAttempts,
          spawnPosChecks,
          spawnCheckFails);
    }
  }
}
