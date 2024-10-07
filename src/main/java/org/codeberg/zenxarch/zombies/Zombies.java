package org.codeberg.zenxarch.zombies;

import folk.sisby.kaleido.api.ReflectiveConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.codeberg.zenxarch.zombies.config.DebugConfig;
import org.codeberg.zenxarch.zombies.config.SpawnConfig;
import org.codeberg.zenxarch.zombies.config.TimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("zombies_zenxarch");

  private static <T extends ReflectiveConfig> T config(String id, Class<T> clazz) {
    return ReflectiveConfig.createToml(
        FabricLoader.getInstance().getConfigDir(), "zenxarch_zombies", id, clazz);
  }

  public static final SpawnConfig SPAWN_CONFIG = config("spawn", SpawnConfig.class);
  public static final DebugConfig DEBUG_CONFIG = config("debug", DebugConfig.class);
  public static final TimeConfig TIME_CONFIG = config("time", TimeConfig.class);

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
  }
}
