package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;
import org.codeberg.zenxarch.mob_variants_api.registry.MobVariants;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public final class ZombieNbtUtils {
  private ZombieNbtUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static final String ZOMBIE_ID_KEY = "zenxarch_zombie_id";

  public static Optional<RegistryEntry<MobVariant>> getVariantFromView(World world, ReadView view) {
    return MobVariants.readVariant(view, ZOMBIE_ID_KEY);
  }

  public static void setVariantToView(
      World world, WriteView view, @Nullable RegistryEntry<MobVariant> variant) {
    MobVariants.putVariant(view, ZOMBIE_ID_KEY, variant);
  }

  public static Optional<Entity> loadFromView(ReadView view, World world) {
    return getVariantFromView(world, view).map(variant -> loadFromView(world, variant, view));
  }

  private static ExtendedZombieEntity loadFromView(
      World world, RegistryEntry<MobVariant> variant, ReadView view) {
    var result = new ExtendedZombieEntity(world);
    result.readData(view);
    return result;
  }
}
