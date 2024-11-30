package org.codeberg.zenxarch.zombies.entity;

import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistries;

public class ZombieTemplateRegistryHelper {

  private static Optional<Registry<ZombieTemplate>> getRegistry(ServerWorld world) {
    return world.getRegistryManager().getOptional(ZombieRegistries.TEMPLATE_REGISTRY_KEY);
  }

  public static Optional<ExtendedZombieEntity> newZombie(
      ServerWorld world, ZombieTemplate template, BlockPos pos) {
    var zombie = new ExtendedZombieEntity(world, template);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    if (zombie.canSpawn(world)) return Optional.of(zombie);
    return Optional.empty();
  }

  public static Optional<ZombieTemplate> selectTemplate(
      ServerWorld world, Random random, RegistryEntry<Biome> biome) {
    var registry = getRegistry(world);
    if (registry.isEmpty()) return Optional.empty();
    var templates =
        registry.get().stream()
            .filter(t -> t.getWeight() > 0)
            .filter(t -> t.canSpawnIn(biome))
            .toList();
    if (templates.isEmpty()) return Optional.empty();
    var totalWeight = templates.stream().mapToInt(ZombieTemplate::getWeight).sum();
    var selection = random.nextInt(totalWeight);
    for (var t : templates) {
      if (selection < t.getWeight()) return Optional.of(t);
      selection -= t.getWeight();
    }
    return Optional.of(templates.getLast());
  }
}
