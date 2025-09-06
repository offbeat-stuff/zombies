package org.codeberg.zenxarch.mob_variants_api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegisteries;
import org.codeberg.zenxarch.mob_variants_api.spawn_conditions.MobSpawnConditions;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;

public class MobVariantsApiMod implements ModInitializer {
  @Override
  public void onInitialize() {
    MobRegisteries.init();
    MobAttachments.initialize();
    MobSpawnConditions.initialize();
  }

  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }
}
