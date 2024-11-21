package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.registry.tag.BiomeTags;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public interface ZombieTemplates {
  private static LootTableBasedTemplate.Builder defaultBuilder() {
    return LootTableBasedTemplate.builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT);
  }

  private static LootTableBasedTemplate.Builder defaultBuilder(int weight) {
    return LootTableBasedTemplate.builder(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT)
        .withWeight(weight);
  }

  public static final ZombieTemplate COMMON_ZOMBIE =
      register(ZombieRegistry.getDefaultId(), defaultBuilder(100));

  public static final ZombieTemplate SWAPPING_ZOMBIE =
      register(
          "SwapperZombie",
          defaultBuilder().addEffect(new AttackEffect.SwapPositionsAttackEffect()));

  public static final ZombieTemplate FIRE_ZOMBIE =
      register(
          "FireZombie",
          defaultBuilder(10)
              .addEffect(new AttackEffect.IgniteAttackEffect())
              .spawnIn(BiomeTags.SPAWNS_WARM_VARIANT_FROGS));

  public static final ZombieTemplate FREEZE_ZOMBIE =
      register(
          "FreezeZombie",
          defaultBuilder(10)
              .addEffect(new AttackEffect.FreezeAttackEffect())
              .spawnIn(BiomeTags.SPAWNS_COLD_VARIANT_FROGS));

  public static void initialize() {
    ZombieRegistry.freezeRegistry();
  }

  public static ZombieTemplate register(String id, LootTableBasedTemplate.Builder builder) {
    return ZombieRegistry.register(id, builder.build());
  }
}
