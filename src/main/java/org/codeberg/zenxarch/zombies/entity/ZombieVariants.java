package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;

public interface ZombieVariants {
  public static Optional<RegistryEntry.Reference<MobVariant>> getRandomVariantFromPos(
      ServerWorld world, BlockPos pos) {
    return VariantSelectorProvider.select(
        world.getRegistryManager().getOrThrow(MobRegistryKeys.MOB_VARIANT).streamEntries(),
        RegistryEntry::value,
        world.getRandom(),
        SpawnContext.of(world, pos));
  }
}
