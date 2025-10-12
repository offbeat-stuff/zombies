package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public final class ZombieGamerules {

  private ZombieGamerules() {
    throw new IllegalStateException("Utility class");
  }

  public static final CustomGameRuleCategory ZOMBIES_GENERAL =
      new CustomGameRuleCategory(
          Zombies.id("zombies_general"),
          MutableText.of(PlainTextContent.of("Zombies"))
              .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));

  public static final CustomGameRuleCategory ZOMBIES_HEARTS =
      new CustomGameRuleCategory(
          Zombies.id("zombies_hearts"),
          MutableText.of(PlainTextContent.of("Zombies Heart Mechanic"))
              .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));

  private static GameRules.Key<GameRules.IntRule> newGameRule(String name, int defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createIntRule(defaultValue));
  }

  private static GameRules.Key<GameRules.BooleanRule> newGameRule(
      String name, boolean defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createBooleanRule(defaultValue));
  }

  public static final GameRules.Key<GameRules.IntRule> MAX_ZOMBIES = newGameRule("maxZombies", 150);

  public static final GameRules.Key<GameRules.IntRule> SPAWN_SPEED =
      newGameRule("fillZombieCapOverSeconds", 30);

  public static final GameRules.Key<GameRules.BooleanRule> ZOMBIES_BURN_IN_DAYLIGHT =
      newGameRule("zombiesBurnInDaylight", false);

  public static final GameRules.Key<GameRules.BooleanRule> DO_ZOMBIE_SPAWNING =
      newGameRule("doZombieSpawning", true);

  public static final GameRules.Key<GameRules.BooleanRule> ZOMBIE_TARGET_PLAYER_ON_SPAWN =
      newGameRule("zombieTargetPlayerOnSpawn", false);

  public static final GameRules.Key<GameRules.BooleanRule> DO_ZOMBIE_KILLS_BASED_HEARTS =
      GameRuleRegistry.register(
          "doZombieKillsBasedHearts",
          ZOMBIES_HEARTS,
          GameRuleFactory.createBooleanRule(false, (server, rule) -> updateAllPlayers(server)));

  public static final GameRules.Key<GameRules.IntRule> ZOMBIE_KILLS_FOR_MAX_HEARTS =
      GameRuleRegistry.register(
          "zombieKillsForMaxHearts",
          ZOMBIES_HEARTS,
          GameRuleFactory.createIntRule(2500, 1, (server, rule) -> updateAllPlayers(server)));

  public static final GameRules.Key<GameRules.IntRule> MIN_HEARTS =
      GameRuleRegistry.register(
          "zombies.minHearts",
          ZOMBIES_HEARTS,
          GameRuleFactory.createIntRule(3, 1, (server, rule) -> updateAllPlayers(server)));

  public static final GameRules.Key<GameRules.IntRule> MAX_HEARTS =
      GameRuleRegistry.register(
          "zombies.maxHearts",
          ZOMBIES_HEARTS,
          GameRuleFactory.createIntRule(25, 1, (server, rules) -> updateAllPlayers(server)));

  private static void updateAllPlayers(MinecraftServer server) {
    for (var world : server.getWorlds())
      for (var player : world.getPlayers()) ZombieHealth.updatePlayerStats(world, player);
  }

  public static void initialize() {
    /* force load class */
  }
}
