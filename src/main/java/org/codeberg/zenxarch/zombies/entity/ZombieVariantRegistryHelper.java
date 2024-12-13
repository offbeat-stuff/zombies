package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public class ZombieVariantRegistryHelper {

  private static Optional<Registry<ZombieVariant>> getRegistry(ServerWorld world) {
    return world.getRegistryManager().getOptional(ZombieRegistryKeys.ZOMBIE_VARIANT);
  }

  public static Optional<ExtendedZombieEntity> newZombie(
      ServerWorld world, ZombieVariant variant, BlockPos pos) {
    var zombie = new ExtendedZombieEntity(world, variant);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    if (zombie.canSpawn(world)) return Optional.of(zombie);
    return Optional.empty();
  }

  public static Optional<ZombieVariant> getRandomVariantFromBiome(
      ServerWorld world, Random random, RegistryEntry<Biome> biome, ExtendedDifficulty difficulty) {
    var registry = getRegistry(world);
    if (registry.isEmpty()) return Optional.empty();
    var variants =
        registry.get().stream()
            .filter(t -> t.getWeight(difficulty) > 0)
            .filter(t -> t.canSpawnIn(biome))
            .toList();
    if (variants.isEmpty()) return Optional.empty();
    var totalWeight = variants.stream().mapToInt(v -> v.getWeight(difficulty)).sum();
    var selection = random.nextInt(totalWeight);
    for (var variant : variants) {
      var weight = variant.getWeight(difficulty);
      if (selection < weight) return Optional.of(variant);
      selection -= weight;
    }
    return Optional.of(variants.getLast());
  }
}
