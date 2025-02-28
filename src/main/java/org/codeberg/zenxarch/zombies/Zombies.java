package org.codeberg.zenxarch.zombies;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootConditionTypes;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootFunctionTypes;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootNumberProviderTypes;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger("zombies_zenxarch");

  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }

  public static final AttachmentType<Identifier> ZOMBIE_VARIANT_TEXTURE_OVERRIDE =
      AttachmentRegistry.create(
          id("zombie_variant_texture_override"),
          builder ->
              builder
                  .persistent(Identifier.CODEC)
                  .syncWith(Identifier.PACKET_CODEC, AttachmentSyncPredicate.all()));

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
    ZombieRegistries.init();
    ZombieGamerules.initialize();
    ZombieLootNumberProviderTypes.initialize();
    ZombieLootFunctionTypes.initialize();
    ZombieLootConditionTypes.initialize();
  }
}
