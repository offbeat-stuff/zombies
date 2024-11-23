package org.codeberg.zenxarch.zombies.datagen

import com.mojang.serialization.Lifecycle
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.registry.Registerable
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryEntryLookup
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.entry.RegistryEntry
import java.util.concurrent.CompletableFuture

class ZDynamicRegistryProvider(output: FabricDataOutput?, registriesFuture: CompletableFuture<WrapperLookup?>?) :
    FabricDynamicRegistryProvider(output, registriesFuture) {
    override fun getName(): String {
        return "zenxarch Dynamic Registries"
    }

    override fun configure(registries: WrapperLookup, entries: Entries) {
        ZEnchantmentProviders.bootstrap(createRegisterable(registries, entries))
    }

    companion object {
        private fun <T> createRegisterable(
            registries: WrapperLookup, entries: Entries
        ): Registerable<T> {
            return object : Registerable<T> {
                override fun register(key: RegistryKey<T>, value: T, lifecycle: Lifecycle): RegistryEntry.Reference<T> {
                    return entries.add(key, value) as RegistryEntry.Reference<T>
                }

                override fun <S> getRegistryLookup(
                    registryRef: RegistryKey<out Registry<out S>?>
                ): RegistryEntryLookup<S> {
                    return registries.getOrThrow(registryRef)
                }
            }
        }
    }
}
