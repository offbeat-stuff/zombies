package org.codeberg.zenxarch.zombies.datagen.dynamic;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;

public interface DynamicRegistryInitializer<T> {
  default RegistryKey<T> of(String id) {
    return RegistryKey.of(getRegistryKey(), Zombies.id(id));
  }

  void bootstrap(Registerable<T> registry);

  RegistryKey<? extends Registry<T>> getRegistryKey();
}
