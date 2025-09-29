package org.codeberg.zenxarch.zombies.datagen;

import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import org.codeberg.zenxarch.zombies.ZombieDatapacks;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZEnchantmentProviderGenerator;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZombieVariantGenerator;
import org.codeberg.zenxarch.zombies.datagen.provider.ZBiomeTagProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZDefaultMobVariantTagProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZDynamicRegistryProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZEnglishLangProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZEntityLootTableProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZLootTableProvider;
import org.codeberg.zenxarch.zombies.datagen.provider.ZMobVariantTagProvider;
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
    var base = generator.createPack();
    base.addProvider(ZEnglishLangProvider::new);
    base.addProvider(ZDynamicRegistryProvider.factory(ZombieVariantGenerator.DEFAULTS_INITIALIZER));
    base.addProvider(ZDefaultMobVariantTagProvider::new);
    var pack = generator.createBuiltinResourcePack(ZombieDatapacks.DEFAULT_PACK);
    for (var init : DYNAMIC_CONTENT) pack.addProvider(ZDynamicRegistryProvider.factory(init));
    pack.addProvider(ZLootTableProvider::new);
    pack.addProvider(ZEntityLootTableProvider::new);
    pack.addProvider(ZBiomeTagProvider::new);
    pack.addProvider(ZMobVariantTagProvider::new);
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
