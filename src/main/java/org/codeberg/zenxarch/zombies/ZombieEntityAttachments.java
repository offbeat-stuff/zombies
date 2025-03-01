package org.codeberg.zenxarch.zombies;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;

public final class ZombieEntityAttachments {
  private ZombieEntityAttachments() {
    throw new IllegalStateException("Utility class");
  }

  public static final AttachmentType<Identifier> ZOMBIE_VARIANT_TEXTURE_OVERRIDE =
      AttachmentRegistry.create(
          Zombies.id("zombie_variant_texture_override"),
          builder -> builder.syncWith(Identifier.PACKET_CODEC, AttachmentSyncPredicate.all()));

  public static void initialize() {
    // Force load class
  }
}
