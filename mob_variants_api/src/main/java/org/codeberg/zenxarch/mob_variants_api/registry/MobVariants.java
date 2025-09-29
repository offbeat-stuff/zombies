package org.codeberg.zenxarch.mob_variants_api.registry;

import java.util.Optional;
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
}
