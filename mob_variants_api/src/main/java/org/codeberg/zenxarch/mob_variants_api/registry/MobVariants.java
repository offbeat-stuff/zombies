package org.codeberg.zenxarch.mob_variants_api.registry;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Optional;
import java.util.Set;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
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

  public static Optional<RegistryEntry<MobVariant>> select(
      ServerWorld world, BlockPos pos, TagKey<MobVariant> tag) {
    return world
        .getRegistryManager()
        .getOrThrow(MobRegistryKeys.MOB_VARIANT)
        .getOptional(tag)
        .flatMap(
            entryList ->
                VariantSelectorProvider.select(
                    entryList.stream(),
                    RegistryEntry::value,
                    world.getRandom(),
                    SpawnContext.of(world, pos)));
  }

  public static void putVariant(
      WriteView view, String key, @Nullable RegistryEntry<MobVariant> variantEntry) {
    view.putNullable(key, MobVariant.ENTRY_CODEC, variantEntry);
  }

  public static Optional<RegistryEntry<MobVariant>> readVariant(ReadView view, String key) {
    return view.read(key, MobVariant.ENTRY_CODEC);
  }

  private static final Set<RegistryEntry<MobVariant>> entriesTemp = new ObjectOpenHashSet<>();

  public static void updateAttachments(AttachmentTarget target, RegistryEntry<MobVariant> variant) {
    if (variant == null) return;
    updateAttachmentsImpl(target, variant);
    entriesTemp.clear();
  }

  private static void updateAttachmentsImpl(
      AttachmentTarget target, RegistryEntry<MobVariant> variant) {
    if (!entriesTemp.add(variant)) return;
    variant
        .value()
        .components()
        .forEach((attachment, value) -> updateAttachment(target, attachment, value));
    variant.value().template().ifPresent(template -> updateAttachmentsImpl(target, template));
  }

  @SuppressWarnings("unchecked")
  private static <T> void updateAttachment(
      AttachmentTarget target, AttachmentType<?> attachment, Object value) {
    if (!target.hasAttached(attachment))
      target.setAttached((AttachmentType<Object>) attachment, value);
  }
}
