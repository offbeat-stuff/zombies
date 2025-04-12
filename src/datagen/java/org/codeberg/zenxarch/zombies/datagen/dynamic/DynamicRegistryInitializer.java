package org.codeberg.zenxarch.zombies.datagen.dynamic;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder.BootstrapFunction;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;

public record DynamicRegistryInitializer<T>(
    RegistryKey<? extends Registry<T>> key, BootstrapFunction<T> bootstrap) {
  public RegistryKey<T> of(String id) {
    return RegistryKey.of(key, Zombies.id(id));
  }
}
