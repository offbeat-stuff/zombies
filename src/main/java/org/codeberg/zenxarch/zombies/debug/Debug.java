package org.codeberg.zenxarch.zombies.debug;

import static org.codeberg.zenxarch.zombies.Zombies.LOGGER;

public class Debug {

  private int spawnAttempts = 0;
  private int successfulSpawnAttempts = 0;

  private int spawnPosChecks = 0;

  public Debug() {}

  public void spawnCheck() { spawnPosChecks++; }

  public void attemptedSpawn(boolean successful) {
    spawnAttempts++;
    successfulSpawnAttempts += successful ? 1 : 0;
    if (spawnAttempts == (20 * 60 * 5)) {
      LOGGER.info(
          "Successful Attemps : {} , Total Attempts: {},spawnChecks : {}",
          successfulSpawnAttempts, spawnAttempts, spawnPosChecks);
    }
  }
}
