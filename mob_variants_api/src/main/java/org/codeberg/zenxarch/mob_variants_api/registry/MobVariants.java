package org.codeberg.zenxarch.mob_variants_api.registry;

import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.Variants;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.jetbrains.annotations.Nullable;

public final class MobVariants {
  private MobVariants() {
    throw new IllegalStateException("Utility class");
  }

  public static Optional<RegistryEntry.Reference<MobVariant>> select(
      ServerWorld world, BlockPos pos) {
    return Variants.select(SpawnContext.of(world, pos), MobRegistryKeys.MOB_VARIANT);
  }

  public static void putVariant(
      WriteView view, String key, @Nullable RegistryEntry<MobVariant> variantEntry) {
    view.putNullable(key, MobVariant.ENTRY_CODEC, variantEntry);
  }

  public static Optional<RegistryEntry<MobVariant>> readVariant(ReadView view, String key) {
    return view.read(key, MobVariant.ENTRY_CODEC);
  }

  public static void updateAttachments(AttachmentTarget target, MobVariant variant) {
    if (variant == null) return;
    variant
        .components()
        .forEach((attachment, value) -> updateAttachment(target, attachment, value));
    variant.template().ifPresent(template -> updateAttachments(target, template.value()));
  }

  @SuppressWarnings("unchecked")
  private static <T> void updateAttachment(
      AttachmentTarget target, AttachmentType<?> attachment, Object value) {
    if (!target.hasAttached(attachment))
      target.setAttached((AttachmentType<Object>) attachment, value);
  }
}
