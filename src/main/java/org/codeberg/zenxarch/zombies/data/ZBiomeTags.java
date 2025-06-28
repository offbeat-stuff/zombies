package org.codeberg.zenxarch.zombies.data;

import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZBiomeTags {
  public static final TagKey<Biome> WITHOUT_ZOMBIE_APOCALYPSE = of("without_zombie_apocalypse");
  public static final TagKey<Biome> WITH_FLAME_ZOMBIES = of("with_flame_zombies");
  public static final TagKey<Biome> WITH_FROST_ZOMBIES = of("with_frost_zombies");
  public static final TagKey<Biome> WITH_AXE_ZOMBIES = of("with_axe_zombies");
  public static final TagKey<Biome> WITH_SWAMP_ZOMBIES = of("with_swamp_zombies");

  private static TagKey<Biome> of(String id) {
    return TagKey.of(RegistryKeys.BIOME, Zombies.id(id));
  }
}
