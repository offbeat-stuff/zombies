package org.codeberg.zenxarch.zombies.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.GameRules;
import org.codeberg.zenxarch.zombies.Zombies;

public class ZEnglishLangProvider extends FabricLanguageProvider {

  public ZEnglishLangProvider(
      FabricDataOutput dataOutput,
      CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
    super(dataOutput, "en_us", registryLookup);
  }

  @Override
  public void generateTranslations(
      WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
    addGameruleTranslation(
        translationBuilder,
        Zombies.BASE_ZOMBIES,
        "Minimum number of zombies to spawn",
        "Zombies mod tries to spawn at least this many zombies (around a player)");
    addGameruleTranslation(
        translationBuilder,
        Zombies.MAX_ZOMBIES,
        "Maximum (* difficulty) numbers of zombies to spawn",
        "This number * globalDifficulty (easy -> 1.0,normal -> 2.0,hard -> 3.0) is the maximum"
            + " amount of zombies to spawned around a player");
    addGameruleTranslation(
        translationBuilder,
        Zombies.SPAWN_SPEED,
        "Fill zombie cap over seconds",
        "The mod tries to spawn the target amount of zombies (difficulty dependent) over this many"
            + " seconds assuming 1% of positions are spawnable");
  }

  public static void addGameruleTranslation(
      TranslationBuilder translationBuilder,
      GameRules.Key<?> gamerule,
      String name,
      String description) {
    translationBuilder.add(gamerule.getTranslationKey(), name);
    translationBuilder.add(gamerule.getTranslationKey() + ".description", description);
  }
}
