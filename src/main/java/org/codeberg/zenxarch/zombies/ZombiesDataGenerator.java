package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.codeberg.zenxarch.zombies.datagen.ZDynamicRegistryProvider;
import org.codeberg.zenxarch.zombies.datagen.ZEnglishLangProvider;
import org.codeberg.zenxarch.zombies.loot_table.ZLootTableProvider;

public class ZombiesDataGenerator implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    var pack = generator.createPack();
    pack.addProvider(ZDynamicRegistryProvider::new);
    pack.addProvider(ZEnglishLangProvider::new);
    pack.addProvider(ZLootTableProvider::new);
  }
}
