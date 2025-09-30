package org.codeberg.zenxarch.zombies.datagen.dynamic;

import java.util.HashMap;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;

public final class ZombieVariantGenerator {

  public static final DynamicRegistryInitializer<MobVariant> INITIALIZER =
      new DynamicRegistryInitializer<>(
          MobRegistryKeys.MOB_VARIANT, ZombieVariantGenerator::bootstrap);

  public static final RegistryKey<MobVariant> COMMON = INITIALIZER.of("default");

  public static void bootstrap(Registerable<MobVariant> registry) {
    registry.register(
        COMMON, new MobVariant(new HashMap<>(), SpawnConditionSelectors.createFallback(1)));
  }
}
