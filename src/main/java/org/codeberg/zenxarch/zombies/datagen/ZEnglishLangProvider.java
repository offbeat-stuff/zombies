package org.codeberg.zenxarch.zombies.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
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
    translationBuilder.add(
        Zombies.BASE_ZOMBIES.getTranslationKey(), "Minimum numbers of zombies to spawn");
    translationBuilder.add(
        Zombies.MAX_ZOMBIES.getTranslationKey(),
        "Maximum (* difficulty) numbers of zombies to spawn");
  }
}
