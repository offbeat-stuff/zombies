package org.codeberg.zenxarch.zombies.datagen;

import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.RegistryBuilder;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZombieVariantGenerator;
import org.codeberg.zenxarch.zombies.datagen.provider.ZDefaultMobVariantTagProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZDynamicRegistryProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZEnglishLangProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombiesDataGenerator implements DataGeneratorEntrypoint {

  public static List<DynamicRegistryInitializer<?>> DYNAMIC_CONTENT =
      List.of(ZombieVariantGenerator.INITIALIZER);

  public static final String MODID = "zombies_zenxarch_datagen";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    var base = generator.createPack();
    for (var init : DYNAMIC_CONTENT) base.addProvider(ZDynamicRegistryProvider.factory(init));
    base.addProvider(ZEnglishLangProvider::new);
    base.addProvider(ZDefaultMobVariantTagProvider::new);
  }

  private <T> void addRegistryBuilder(
      RegistryBuilder registryBuilder, DynamicRegistryInitializer<T> builder) {
    registryBuilder.addRegistry(builder.key(), builder.bootstrap());
  }

  @Override
  public void buildRegistry(RegistryBuilder registryBuilder) {
    if (FabricLoader.getInstance().isModLoaded("default_zombies_zenxarch_datagen")) return;
    for (var content : DYNAMIC_CONTENT) addRegistryBuilder(registryBuilder, content);
  }

  @Override
  public @Nullable String getEffectiveModId() {
    return Zombies.MODID;
  }
}
