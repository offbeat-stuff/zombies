package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public final class ZombieNbtUtils {
  private ZombieNbtUtils() {
    throw new IllegalStateException("Utility class");
  }

  private static final String ZOMBIE_ID_KEY = "zenxarch_zombie_id";

  private static Identifier toId(String id) {
    int i = id.indexOf(":");
    if (i >= 0) {
      String string = id.substring(i + 1);
      if (i != 0) {
        String string2 = id.substring(0, i);
        return Identifier.of(string2, string);
      }
      return Zombies.id(string);
    }
    return Zombies.id(id);
  }

  private static Optional<RegistryEntry<MobVariant>> fromId(World world, Identifier id) {
    return world
        .getRegistryManager()
        .getOrThrow(MobRegistryKeys.MOB_VARIANT)
        .getEntry(id)
        .map(Function.identity());
  }

  public static Optional<RegistryEntry<MobVariant>> getVariantFromView(World world, ReadView view) {
    var idKey = view.getOptionalString(ZOMBIE_ID_KEY);
    if (idKey.isEmpty()) return Optional.empty();
    return switch (idKey.get()) {
      case "" -> Optional.empty();
      case String id -> {
        try {
          yield fromId(world, toId(id));
        } catch (Exception e) {
          Zombies.LOGGER.info("Exception caught: {}", e.getMessage());
          yield Optional.empty();
        }
      }
    };
  }

  public static void setVariantToView(
      World world, WriteView view, @Nullable RegistryEntry<MobVariant> variant) {
    var registry = world.getRegistryManager().getOptional(MobRegistryKeys.MOB_VARIANT);
    if (registry.isEmpty()) return;
    if (variant == null) return;
    view.putString(ZombieNbtUtils.ZOMBIE_ID_KEY, variant.getIdAsString());
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
