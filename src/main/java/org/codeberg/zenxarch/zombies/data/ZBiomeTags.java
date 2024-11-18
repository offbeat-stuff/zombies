package org.codeberg.zenxarch.zombies.data;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.Zombies;

public class ZBiomeTags {
  public static final TagKey<Biome> WITHOUT_ZOMBIE_APOCALYPSE = of("without_zombie_apocalypse");

  private static TagKey<Biome> of(String id) {
    return TagKey.of(RegistryKeys.BIOME, Zombies.id(id));
  }
}
