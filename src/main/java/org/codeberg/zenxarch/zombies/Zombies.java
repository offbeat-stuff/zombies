package org.codeberg.zenxarch.zombies;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("zombies_zenxarch");

  public static final CustomGameRuleCategory ZOMBIES_GENERAL =
      new CustomGameRuleCategory(id("general"), Text.of("Zombies"));

  public static GameRules.Key<GameRules.IntRule> newGameRule(String name, int defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createIntRule(defaultValue));
  }

  public static final GameRules.Key<GameRules.IntRule> BASE_ZOMBIES =
      newGameRule("startingNumberOfZombies", 10);

  public static final GameRules.Key<GameRules.IntRule> MAX_ZOMBIES =
      newGameRule("maxNumberOfZombiesAdjustedByDifficulty", 50);

  public static final GameRules.Key<GameRules.IntRule> SPAWN_SPEED =
      newGameRule("fillZombieCapOverSeconds", 30);

  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
  }
}
