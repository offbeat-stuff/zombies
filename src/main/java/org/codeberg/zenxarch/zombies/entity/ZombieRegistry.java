package org.codeberg.zenxarch.zombies.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;

public class ZombieRegistry {

  public static String getDefaultId() {
    return "BaseZombie";
  }

  private static Map<String, ZombieTemplate> registry = new HashMap<>();

  public static ZombieTemplate register(String id, ZombieTemplate template) {
    registry.put(id, template);
    return template;
  }

  public static void freezeRegistry() {
    registry = new ImmutableMap.Builder<String, ZombieTemplate>().putAll(registry).build();
  }

  public static ZombieTemplate getTemplate(String id) {
    if (!registry.containsKey(id)) return getDefault();
    return registry.get(id);
  }

  public static Collection<ZombieTemplate> templates() {
    return registry.values();
  }

  public static String getId(ZombieTemplate template) {
    for (var entry : registry.entrySet()) {
      if (entry.getValue().equals(template)) return entry.getKey();
    }
    return getDefaultId();
  }

  public static ZombieTemplate getDefault() {
    return ZombieTemplates.COMMON_ZOMBIE;
  }

  public static Optional<ExtendedZombieEntity> newZombie(
      ServerWorld world, ZombieTemplate template, BlockPos pos) {
    var zombie = new ExtendedZombieEntity(world, template);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    if (zombie.canSpawn(world)) return Optional.of(zombie);
    return Optional.empty();
  }

  public static Optional<ZombieTemplate> selectTemplate(Random random, RegistryEntry<Biome> biome) {
    var templates =
        templates().stream()
            .map(t -> t instanceof LootTableBasedTemplate def ? def : null)
            .filter(Objects::nonNull)
            .filter(t -> t.weight() > 0)
            .filter(t -> t.biomePredicate().test(biome))
            .toList();
    if (templates.isEmpty()) return Optional.empty();
    var totalWeight = templates.stream().mapToInt(LootTableBasedTemplate::weight).sum();
    var selection = random.nextInt(totalWeight);
    for (var t : templates) {
      if (selection < t.weight()) return Optional.of(t);
      selection -= t.weight();
    }
    return Optional.of(templates.getLast());
  }
}
