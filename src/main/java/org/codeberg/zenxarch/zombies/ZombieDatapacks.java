package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;

public final class ZombieDatapacks {
  private ZombieDatapacks() {
    throw new IllegalStateException("Utility class");
  }

  public static final Identifier DEFAULT_PACK = Zombies.id("default_zombies");

  public static void registerDatapacks(ModContainer container) {
    ResourceManagerHelper.registerBuiltinResourcePack(
        DEFAULT_PACK, container, ResourcePackActivationType.DEFAULT_ENABLED);
  }
}
