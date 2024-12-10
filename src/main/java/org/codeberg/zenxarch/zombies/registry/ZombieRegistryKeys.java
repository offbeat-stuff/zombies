package org.codeberg.zenxarch.zombies.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.entity.ZombieVariant;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect;

public interface ZombieRegistryKeys {
  private static <T> RegistryKey<Registry<T>> getRegistryKey(String id) {
    return RegistryKey.ofRegistry(Zombies.id(id));
  }

  public static final RegistryKey<Registry<MapCodec<? extends SingleLivingEffect>>> SINGLE_LIVING =
      getRegistryKey("single_living_effect");
  public static final RegistryKey<Registry<MapCodec<? extends ZombieEffect>>> ZOMBIE_EFFECT =
      getRegistryKey("zombie_effect");

  public static final RegistryKey<Registry<ZombieVariant>> ZOMBIE_VARIANT =
      getRegistryKey("zombie_variant");
}
