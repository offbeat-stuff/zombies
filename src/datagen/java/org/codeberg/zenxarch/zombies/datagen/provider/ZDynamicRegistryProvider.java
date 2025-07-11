package org.codeberg.zenxarch.zombies.datagen.provider;

import com.mojang.serialization.Lifecycle;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack.RegistryDependentFactory;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntry.Reference;
import org.codeberg.zenxarch.zombies.datagen.dynamic.DynamicRegistryInitializer;

public class ZDynamicRegistryProvider<T> extends FabricDynamicRegistryProvider {

  private final DynamicRegistryInitializer<T> initializer;

  public ZDynamicRegistryProvider(
      DynamicRegistryInitializer<T> initializer,
      FabricDataOutput output,
      CompletableFuture<WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
    this.initializer = initializer;
  }

  @Override
  public String getName() {
    return "zenxarch Dynamic Registries" + initializer.key();
  }

  @Override
  protected void configure(WrapperLookup registries, Entries entries) {
    initializer.bootstrap().run(createRegisterable(registries, entries));
  }

  private Registerable<T> createRegisterable(WrapperLookup registries, Entries entries) {
    return new Registerable<T>() {

      @Override
      public Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
        return (RegistryEntry.Reference<T>) entries.add(key, value);
      }

      @Override
      public <S> RegistryEntryLookup<S> getRegistryLookup(
          RegistryKey<? extends Registry<? extends S>> registryRef) {
        return registries.getOrThrow(registryRef);
      }
    };
  }

  public static <T> RegistryDependentFactory<ZDynamicRegistryProvider<T>> factory(
      DynamicRegistryInitializer<T> initializer) {
    return (output, registeriesFuture) ->
        new ZDynamicRegistryProvider<>(initializer, output, registeriesFuture);
  }
}
