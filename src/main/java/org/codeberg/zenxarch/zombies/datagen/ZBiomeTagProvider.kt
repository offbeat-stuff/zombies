package org.codeberg.zenxarch.zombies.datagen

import net.minecraft.data.DataOutput
import net.minecraft.data.server.tag.TagProvider
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.registry.tag.BiomeTags
import net.minecraft.registry.tag.TagKey
import net.minecraft.world.biome.Biome
import org.codeberg.zenxarch.zombies.data.ZBiomeTags
import java.util.concurrent.CompletableFuture

class ZBiomeTagProvider(output: DataOutput, registriesFuture: CompletableFuture<WrapperLookup>) :
    TagProvider<Biome?>(output, RegistryKeys.BIOME, registriesFuture) {
    override fun configure(registries: WrapperLookup) {
        getOrCreateTagBuilder(ZBiomeTags.WITHOUT_ZOMBIE_APOCALYPSE as? TagKey<Biome?>)
            .addOptionalTag(BiomeTags.WITHOUT_ZOMBIE_SIEGES.id())
            .addOptionalTag(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE.id())
    }
}
