package org.codeberg.zenxarch.zombies.datagen;

import java.util.concurrent.CompletableFuture;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.tag.TagProvider;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;

public class ZBiomeTagProvider extends TagProvider<Biome> {
  public ZBiomeTagProvider(
      DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, RegistryKeys.BIOME, registriesFuture);
  }

  @Override
  protected void configure(WrapperLookup registries) {
    this.getOrCreateTagBuilder(ZBiomeTags.WITHOUT_ZOMBIE_APOCALYPSE)
        .addOptionalTag(BiomeTags.WITHOUT_ZOMBIE_SIEGES.id())
        .addOptionalTag(BiomeTags.ANCIENT_CITY_HAS_STRUCTURE.id());
  }
}
