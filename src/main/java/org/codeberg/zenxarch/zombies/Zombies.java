package org.codeberg.zenxarch.zombies;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.zombies.data.entity.MobAttachments;
import org.codeberg.zenxarch.zombies.data.spawn_conditions.ZombieSpawnConditions;
import org.codeberg.zenxarch.zombies.loot_table.condition.ZombieLootConditionTypes;
import org.codeberg.zenxarch.zombies.loot_table.function.ZombieLootFunctionTypes;
import org.codeberg.zenxarch.zombies.loot_table.number_provider.ZombieLootNumberProviderTypes;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {

  public static final String MODID = "zombies_zenxarch";
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
    ZombieRegistries.init();
    ZombieGamerules.initialize();
    ZombieLootNumberProviderTypes.initialize();
    ZombieLootFunctionTypes.initialize();
    ZombieLootConditionTypes.initialize();
    MobAttachments.initialize();
    ZombieSpawnConditions.initialize();
  }
}
