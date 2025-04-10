package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.entity.variant.ZombieVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public interface ZombieVariants {
  public static Optional<RegistryEntry.Reference<ZombieVariant>> getRandomVariantFromPos(
      ServerWorld world, BlockPos pos) {
    return VariantSelectorProvider.select(
        world.getRegistryManager().getOrThrow(ZombieRegistryKeys.ZOMBIE_VARIANT).streamEntries(),
        RegistryEntry::value,
        world.getRandom(),
        SpawnContext.of(world, pos));
  }
}
