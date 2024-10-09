package org.codeberg.zenxarch.zombies.debug;

import static org.codeberg.zenxarch.zombies.Zombies.DEBUG_CONFIG;
import static org.codeberg.zenxarch.zombies.Zombies.LOGGER;

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

  public void attemptedSpawn(boolean successful) {
    spawnAttempts++;
    successfulSpawnAttempts += successful ? 1 : 0;
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
