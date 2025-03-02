package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.entity.variant.ZombieVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public interface ZombieVariantRegistryHelper {

  public static Optional<ExtendedZombieEntity> newZombie(ServerWorld world, BlockPos pos) {
    var zombie = new ExtendedZombieEntity(world);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    if (zombie.canSpawn(world)) return Optional.of(zombie);
    return Optional.empty();
  }

  public static Optional<RegistryEntry.Reference<ZombieVariant>> getRandomVariantFromPos(
      ServerWorld world, BlockPos pos) {
    return VariantSelectorProvider.select(
        world.getRegistryManager().getOrThrow(ZombieRegistryKeys.ZOMBIE_VARIANT).streamEntries(),
        RegistryEntry::value,
        world.getRandom(),
        SpawnContext.of(world, pos));
  }
}
