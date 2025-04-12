package org.codeberg.zenxarch.zombies.datagen;

import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZEnchantmentProviderGenerator;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZombieVariantGenerator;
import org.codeberg.zenxarch.zombies.datagen.provider.ZBiomeTagProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZDynamicRegistryProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZEnglishLangProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZLootTableProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZombiesDataGenerator implements DataGeneratorEntrypoint {

  public static final String MODID = "zombies_zenxarch_datagen";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  public static List<DynamicRegistryInitializer<?>> DYNAMIC_CONTENT =
      List.of(ZEnchantmentProviderGenerator.INITIALIZER, ZombieVariantGenerator.INITIALIZER);

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    var pack = generator.createPack();
    pack.addProvider(ZDynamicRegistryProvider::new);
    pack.addProvider(ZEnglishLangProvider::new);
    pack.addProvider(ZLootTableProvider::new);
    pack.addProvider(ZBiomeTagProvider::new);
  }

  private <T> void addRegistryBuilder(
      RegistryBuilder registryBuilder, DynamicRegistryInitializer<T> builder) {
    registryBuilder.addRegistry(builder.key(), builder.bootstrap());
  }

  @Override
  public void buildRegistry(RegistryBuilder registryBuilder) {
    for (var content : DYNAMIC_CONTENT) addRegistryBuilder(registryBuilder, content);
  }

  @Override
  public @Nullable String getEffectiveModId() {
    return Zombies.MODID;
  }
}
