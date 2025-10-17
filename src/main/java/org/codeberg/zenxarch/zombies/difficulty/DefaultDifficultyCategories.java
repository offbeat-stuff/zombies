package org.codeberg.zenxarch.zombies.difficulty;

import org.codeberg.zenxarch.zombies.difficulty.category.KillCategory;
import org.codeberg.zenxarch.zombies.difficulty.category.PlayerCategory;
import org.codeberg.zenxarch.zombies.difficulty.category.TimeCategory;

public interface DefaultDifficultyCategories {
  public static void initialize() {
    KillCategory.initialize();
    PlayerCategory.initialize();
    TimeCategory.initialize();
    DifficultyCategory.addDifficultyStage(PlayerCategory.PLAYER_CATEGORY, (a, b) -> a * b);
    DifficultyCategory.addDifficultyStage(
        TimeCategory.DAYS_CATEGORY, (a, b) -> (a * 0.3 + a * b * 0.5 + b * 0.1) / 0.9);
    DifficultyCategory.addDifficultyStage(KillCategory.KILL_CATEGORY, (a, b) -> a * 0.9 + b * 0.1);
    DifficultyCategory.addDifficultyStage(TimeCategory.TIME_CATEGORY, (a, b) -> a * b);
  }
}
