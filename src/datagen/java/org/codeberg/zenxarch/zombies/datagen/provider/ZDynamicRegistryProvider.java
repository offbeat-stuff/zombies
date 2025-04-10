package org.codeberg.zenxarch.zombies.datagen.provider;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import org.codeberg.zenxarch.zombies.datagen.ZombiesDataGenerator;

public class ZDynamicRegistryProvider extends FabricDynamicRegistryProvider {
  public ZDynamicRegistryProvider(
      FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  public String getName() {
    return "zenxarch Dynamic Registries";
  }

  private <T> List<RegistryEntry<T>> addAll(
      WrapperLookup registries, Entries entries, RegistryKey<? extends Registry<T>> registryKey) {
    var registry = registries.getOrThrow(registryKey);
    return registry
        .streamKeys()
        .filter(key -> key.getValue().getNamespace().equals(genNamespace()))
        .map(key -> entries.add(registry, key))
        .toList();
  }

  @Override
  protected void configure(WrapperLookup registries, Entries entries) {
    for (var content : ZombiesDataGenerator.DYNAMIC_CONTENT)
      addAll(registries, entries, content.getRegistryKey());
  }

  private String genNamespace() {
    return "zenxarch";
  }
}
