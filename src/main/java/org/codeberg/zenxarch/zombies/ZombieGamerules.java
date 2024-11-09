package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

public class ZombieGamerules {
  public static final CustomGameRuleCategory ZOMBIES_GENERAL =
      new CustomGameRuleCategory(Zombies.id("general"), Text.of("Zombies"));

  public static GameRules.Key<GameRules.IntRule> newGameRule(String name, int defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createIntRule(defaultValue));
  }

  public static GameRules.Key<GameRules.BooleanRule> newGameRule(
      String name, boolean defaultValue) {
    return GameRuleRegistry.register(
        name, ZOMBIES_GENERAL, GameRuleFactory.createBooleanRule(defaultValue));
  }

  public static final GameRules.Key<GameRules.IntRule> MAX_ZOMBIES = newGameRule("maxZombies", 150);

  public static final GameRules.Key<GameRules.IntRule> SPAWN_SPEED =
      newGameRule("fillZombieCapOverSeconds", 30);

  public static final GameRules.Key<GameRules.BooleanRule> ZOMBIES_BURN_IN_DAYLIGHT =
      newGameRule("zombiesBurnInDaylight", false);
}
