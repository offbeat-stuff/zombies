package org.codeberg.zenxarch.zombies.datagen;

import com.mojang.serialization.Lifecycle;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;

public class ZDynamicRegistryProvider extends FabricDynamicRegistryProvider {
  public ZDynamicRegistryProvider(
      FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
  }

  @Override
  public String getName() {
    return "zenxarch Dynamic Registries";
  }

  @Override
  protected void configure(WrapperLookup registries, Entries entries) {
    ZEnchantmentProviders.bootstrap(createRegisterable(registries, entries));
    ZombieVariants.bootstrap(createRegisterable(registries, entries));
  }

  private static <T> Registerable<T> createRegisterable(
      RegistryWrapper.WrapperLookup registries, Entries entries) {
    return new Registerable<T>() {
      @Override
      public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
        return (RegistryEntry.Reference<T>) entries.add(key, value);
      }

      @Override
      public <S> RegistryEntryLookup<S> getRegistryLookup(
          RegistryKey<? extends Registry<? extends S>> registryRef) {
        return registries.getWrapperOrThrow(registryRef);
      }
    };
  }
}
