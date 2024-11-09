package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;

public class ZombieGamerules {
  public static final CustomGameRuleCategory ZOMBIES_GENERAL =
      new CustomGameRuleCategory(
          Zombies.id("general"),
          MutableText.of(PlainTextContent.of("Zombies"))
              .setStyle(Style.EMPTY.withColor(Formatting.YELLOW)));

  public static GameRules.Key<GameRules.IntRule> newGameRule(String name, int defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createIntRule(defaultValue));
  }

  public static GameRules.Key<GameRules.BooleanRule> newGameRule(
      String name, boolean defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createBooleanRule(defaultValue));
  }

  public static final GameRules.Key<GameRules.IntRule> MAX_ZOMBIES =
      ZombieGamerules.newGameRule("maxZombies", 150);

  public static final GameRules.Key<GameRules.IntRule> SPAWN_SPEED =
      ZombieGamerules.newGameRule("fillZombieCapOverSeconds", 30);

  public static final GameRules.Key<GameRules.BooleanRule> ZOMBIES_BURN_IN_DAYLIGHT =
      ZombieGamerules.newGameRule("zombiesBurnInDaylight", false);

  public static final GameRules.Key<GameRules.BooleanRule> DO_ZOMBIE_SPAWNING =
      ZombieGamerules.newGameRule("doZombieSpawning", true);

  public static void initialize() {}
}
