package org.codeberg.zenxarch.zombies.debug;

import static org.codeberg.zenxarch.zombies.Zombies.LOGGER;

public abstract class Debug {

  private static int spawnAttempts = 0;
  private static int successfulSpawnAttempts = 0;

  private static int spawnPosChecks = 0;
  private static int postedAt = 0;

  public static void spawnCheck() { spawnPosChecks++; }

  public static void attemptedSpawn(boolean successful) {
    spawnAttempts++;
    successfulSpawnAttempts += successful ? 1 : 0;
    if (successfulSpawnAttempts == postedAt) {
      return;
    }
    if (successfulSpawnAttempts % 10 == 0) {
      postedAt = successfulSpawnAttempts;
      LOGGER.info(
          "Successful Attemps : {} , Total Attempts: {},spawnChecks : {}",
          successfulSpawnAttempts, spawnAttempts, spawnPosChecks);
    }
  }
}
