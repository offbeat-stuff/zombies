package org.codeberg.zenxarch.mob_variants_api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class MobVariantsApiMod implements ModInitializer {
  @Override
  public void onInitialize() {}

  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }
}
