package org.codeberg.zenxarch.zombies

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import org.codeberg.zenxarch.zombies.datagen.ZBiomeTagProvider
import org.codeberg.zenxarch.zombies.datagen.ZDynamicRegistryProvider
import org.codeberg.zenxarch.zombies.datagen.ZEnglishLangProvider
import org.codeberg.zenxarch.zombies.loot_table.ZLootTableProvider
import java.util.concurrent.CompletableFuture

class ZombiesDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(generator: FabricDataGenerator) {
        val pack = generator.createPack()
        pack.addProvider(::ZDynamicRegistryProvider)
        pack.addProvider(::ZEnglishLangProvider)
        pack.addProvider(::ZLootTableProvider)
        pack.addProvider(::ZBiomeTagProvider)
    }
}
