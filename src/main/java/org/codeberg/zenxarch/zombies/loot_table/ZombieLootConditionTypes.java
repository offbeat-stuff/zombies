package org.codeberg.zenxarch.zombies.loot_table;

import com.mojang.serialization.MapCodec;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZombieLootConditionTypes {

  public static final LootConditionType TIME_CHECK =
      register("time_check", TimeCheckLootCondition.CODEC);

  private static LootConditionType register(String id, MapCodec<? extends LootCondition> codec) {
    return Registry.register(
        Registries.LOOT_CONDITION_TYPE, Zombies.id(id), new LootConditionType(codec));
  }

  public static void initialize() {
    // force load class
  }
}
