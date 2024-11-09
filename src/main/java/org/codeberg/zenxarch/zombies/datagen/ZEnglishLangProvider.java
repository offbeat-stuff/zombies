package org.codeberg.zenxarch.zombies.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.GameRules;
import org.codeberg.zenxarch.zombies.ZombieGamerules;

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
        ZombieGamerules.MAX_ZOMBIES,
        "Maximum zombies per player",
        "The max number of zombies that can spawn in a 80 block radius around player");
    addGameruleTranslation(
        translationBuilder,
        ZombieGamerules.SPAWN_SPEED,
        "Fill zombie cap over seconds",
        "The mod tries to spawn the target amount of zombies (difficulty dependent) over this many"
            + " seconds assuming 1% of positions are spawnable");
    addGameruleTranslation(
        translationBuilder,
        ZombieGamerules.ZOMBIES_BURN_IN_DAYLIGHT,
        "Zombies burn in daylight",
        "Should zombies burn in (and not spawn in) daylight. (only affects mod's zombies).");
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
