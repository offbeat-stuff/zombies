package org.codeberg.zenxarch.zombies;

import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.spawning.Spawning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER =
      LoggerFactory.getLogger("zombies_zenxarch");

  public static final Random zrx = Random.create();

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
  }

  public static void spawnZombiesForEachWorld(MinecraftServer server) {
    var world = server.getOverworld();

    for (var player : world.getPlayers(Spawning::isSuitablePlayer)) {
      Spawning.spawnZombieAt(world, player.getBlockPos());
    }
  }
}
