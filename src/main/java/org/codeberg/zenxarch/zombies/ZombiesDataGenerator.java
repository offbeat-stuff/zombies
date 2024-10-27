package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.codeberg.zenxarch.zombies.datagen.ZItemTags;

public class ZombiesDataGenerator implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    var pack = generator.createPack();
    pack.addProvider(ZItemTags::new);
  }
}
