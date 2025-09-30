package org.codeberg.zenxarch.default_zombies.datagen;

import java.util.List;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import org.codeberg.zenxarch.default_zombies.DefaultZombiesMod;
import org.codeberg.zenxarch.default_zombies.datagen.dynamic.ZEnchantmentProviderGenerator;
import org.codeberg.zenxarch.default_zombies.datagen.dynamic.ZombieVariantGenerator;
import org.codeberg.zenxarch.default_zombies.datagen.provider.*;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;
import org.codeberg.zenxarch.zombies.datagen.provider.ZDynamicRegistryProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultZombiesDatagen implements DataGeneratorEntrypoint {

  public static List<DynamicRegistryInitializer<?>> DYNAMIC_CONTENT =
      List.of(ZEnchantmentProviderGenerator.INITIALIZER, ZombieVariantGenerator.INITIALIZER);

  public static final String MODID = "default_zombies_zenxarch_datagen";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    var pack = generator.createBuiltinResourcePack(DefaultZombiesMod.DEFAULT_PACK);
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
