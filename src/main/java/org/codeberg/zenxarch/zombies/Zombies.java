package org.codeberg.zenxarch.zombies;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("zombies_zenxarch");

  // private static <T extends ReflectiveConfig> T config(String id, Class<T> clazz) {
  //   return ReflectiveConfig.createToml(
  //       FabricLoader.getInstance().getConfigDir(), "zenxarch_zombies", id, clazz);
  // }

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
  }
}
