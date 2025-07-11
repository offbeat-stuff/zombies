package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.entity.variant.MobVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public interface ZombieVariants {
  public static Optional<RegistryEntry.Reference<MobVariant>> getRandomVariantFromPos(
      ServerWorld world, BlockPos pos) {

    var entries = world.getRegistryManager().getWrapperOrThrow(ZombieRegistryKeys.MOB_VARIANT).streamEntries().map(RegistryEntry::value).toList();
    var ctx = new SpawnContext(world, pos);
    return entries.stream().filter(
      v -> v.spawnConditions()
    )
  }
}
