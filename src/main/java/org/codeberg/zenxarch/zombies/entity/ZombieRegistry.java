package org.codeberg.zenxarch.zombies.entity;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public class ZombieRegistry {

  public static String getDefaultId() {
    return "BaseZombie";
  }

  private static Map<String, ZombieTemplate> registry = new HashMap<>();
  public static final ZombieTemplate COMMON_ZOMBIE =
      register(
          getDefaultId(), new LootTableBasedTemplate(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT));

  private static ZombieTemplate register(String id, ZombieTemplate template) {
    registry.put(id, template);
    return template;
  }

  public static void initRegistry() {
    registry = new ImmutableMap.Builder<String, ZombieTemplate>().putAll(registry).build();
  }

  public static ZombieTemplate getTemplate(String id) {
    if (!registry.containsKey(id)) return getDefault();
    return registry.get(id);
  }

  public static String getId(ZombieTemplate template) {
    for (var entry : registry.entrySet()) {
      if (entry.getValue().equals(template)) return entry.getKey();
    }
    return getDefaultId();
  }

  public static ZombieTemplate getDefault() {
    return COMMON_ZOMBIE;
  }

  public static Optional<ExtendedZombieEntity> newZombie(
      ServerWorld world, ZombieTemplate template, BlockPos pos) {
    var zombie = new ExtendedZombieEntity(world, template);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    if (zombie.canSpawn(world)) return Optional.of(zombie);
    return Optional.empty();
  }
}
