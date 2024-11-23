package org.codeberg.zenxarch.zombies.data

import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.world.biome.Biome
import org.codeberg.zenxarch.zombies.Zombies.id

object ZBiomeTags {
    val WITHOUT_ZOMBIE_APOCALYPSE: TagKey<Biome> = of("without_zombie_apocalypse")

    private fun of(id: String) = TagKey.of(RegistryKeys.BIOME, id(id))
}
