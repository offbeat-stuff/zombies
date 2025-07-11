package org.codeberg.zenxarch.zombies.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.codeberg.zenxarch.zombies.entity.variant.MobVariant;

public interface ZombieRegistryKeys {
  private static <T> RegistryKey<Registry<T>> getRegistryKey(String id) {
    return RegistryKey.ofRegistry(Zombies.id(id));
  }

  public static final RegistryKey<Registry<MapCodec<? extends LivingEffect>>> LIVING_EFFECT =
      getRegistryKey("living_effect");
  public static final RegistryKey<Registry<MapCodec<? extends MobEffect>>> MOB_EFFECT =
      getRegistryKey("mob_effect");

  public static final RegistryKey<Registry<MobVariant>> MOB_VARIANT = getRegistryKey("mob_variant");
}
