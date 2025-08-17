package org.codeberg.zenxarch.zombies.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.ZModUtils;
import org.codeberg.zenxarch.zombies.variant.MobVariant;
import org.codeberg.zenxarch.zombies.variant.effect.LivingEffect;
import org.codeberg.zenxarch.zombies.variant.effect.MobEffect;

public interface ZombieRegistryKeys {
  private static <T> RegistryKey<Registry<T>> getRegistryKey(String id) {
    return RegistryKey.ofRegistry(ZModUtils.id(id));
  }

  public static final RegistryKey<Registry<MapCodec<? extends LivingEffect>>> LIVING_EFFECT =
      getRegistryKey("living_effect");
  public static final RegistryKey<Registry<MapCodec<? extends MobEffect>>> MOB_EFFECT =
      getRegistryKey("mob_effect");

  public static final RegistryKey<Registry<MobVariant>> MOB_VARIANT = getRegistryKey("mob_variant");
}
