package org.codeberg.zenxarch.mob_variants_api.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect;

public interface MobRegistryKeys {
  private static <T> RegistryKey<Registry<T>> getRegistryKey(String id) {
    return RegistryKey.ofRegistry(MobVariantsApiMod.id(id));
  }

  public static final RegistryKey<Registry<MapCodec<? extends LivingEffect>>> LIVING_EFFECT =
      getRegistryKey("living_effect");
  public static final RegistryKey<Registry<MapCodec<? extends MobEffect>>> MOB_EFFECT =
      getRegistryKey("mob_effect");

  public static final RegistryKey<Registry<MobVariant>> MOB_VARIANT = getRegistryKey("mob_variant");
}
