package org.codeberg.zenxarch.default_zombies;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultZombiesMod implements ModInitializer {

  public static final String MODID = "default_zombies_zenxarch";
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  public static final Identifier DEFAULT_PACK = id("default_zombies");

  @Override
  public void onInitialize() {
    registerDatapacks(FabricLoader.getInstance().getModContainer(MODID).get());
  }

  public static void registerDatapacks(ModContainer container) {
    ResourceManagerHelper.registerBuiltinResourcePack(
        DEFAULT_PACK, container, ResourcePackActivationType.DEFAULT_ENABLED);
  }

  private static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }
}
