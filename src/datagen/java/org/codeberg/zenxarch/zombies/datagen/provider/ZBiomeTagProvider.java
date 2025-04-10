package org.codeberg.zenxarch.zombies.datagen.provider;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.data.ZBiomeTags;

public class ZBiomeTagProvider extends FabricTagProvider<Biome> {
  public ZBiomeTagProvider(
      FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, RegistryKeys.BIOME, registriesFuture);
  }

  @Override
  protected void configure(WrapperLookup registries) {
    this.getOrCreateTagBuilder(ZBiomeTags.WITHOUT_ZOMBIE_APOCALYPSE)
        .addOptionalTag(ConventionalBiomeTags.NO_DEFAULT_MONSTERS);
    this.getOrCreateTagBuilder(ZBiomeTags.WITH_FLAME_ZOMBIES)
        .addOptionalTag(ConventionalBiomeTags.IS_HOT_OVERWORLD);
    this.getOrCreateTagBuilder(ZBiomeTags.WITH_FROST_ZOMBIES)
        .addOptionalTag(ConventionalBiomeTags.IS_COLD_OVERWORLD);
  }
}
