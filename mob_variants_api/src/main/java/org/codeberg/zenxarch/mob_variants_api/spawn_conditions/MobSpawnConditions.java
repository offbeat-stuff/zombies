package org.codeberg.zenxarch.mob_variants_api.spawn_conditions;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;

public final class MobSpawnConditions {
  private MobSpawnConditions() {
    throw new IllegalStateException("Utility class");
  }

  public static void initialize() {
    register("is_day", DaySpawnCondition.CODEC);
    register("is_night", NightSpawnCondition.CODEC);
    register("negate", NegateSpawnCondition.CODEC);
    register("all_of", AllOfSpawnCondition.CODEC);
    register("is_raining", RainingSpawnCondition.CODEC);
    register("is_thundering", ThunderingSpawnCondition.CODEC);
    register("precipitation", PrecipitationSpawnCondition.CODEC);
  }

  private static void register(String id, MapCodec<? extends SpawnCondition> codec) {
    Registry.register(Registries.SPAWN_CONDITION_TYPE, MobVariantsApiMod.id(id), codec);
  }
}
