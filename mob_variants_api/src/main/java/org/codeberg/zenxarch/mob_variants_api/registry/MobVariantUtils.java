package org.codeberg.zenxarch.mob_variants_api.registry;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.Variants;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;

public final class MobVariantUtils {
  private MobVariantUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static Optional<RegistryEntry.Reference<MobVariant>> getRandomVariantFromPos(
      ServerWorld world, BlockPos pos) {
    return Variants.select(SpawnContext.of(world, pos), MobRegistryKeys.MOB_VARIANT);
  }

  public static Identifier toId(String id) {
    int i = id.indexOf(":");
    if (i >= 0) {
      String string = id.substring(i + 1);
      if (i != 0) {
        String string2 = id.substring(0, i);
        return Identifier.of(string2, string);
      }
      return MobVariantsApiMod.id(string);
    }
    return MobVariantsApiMod.id(id);
  }

  public static Optional<RegistryEntry<MobVariant>> getOptionalVariant(World world, Identifier id) {
    return world
        .getRegistryManager()
        .getOrThrow(MobRegistryKeys.MOB_VARIANT)
        .getEntry(id)
        .map(Function.identity());
  }
}
