package org.codeberg.zenxarch.zombies.datagen.provider;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.codeberg.zenxarch.zombies.data.ZMobVariantTags;
import org.codeberg.zenxarch.zombies.datagen.dynamic.ZombieVariantGenerator;

public class ZDefaultMobVariantTagProvider extends FabricTagProvider<MobVariant> {
  public ZDefaultMobVariantTagProvider(
      FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, MobRegistryKeys.MOB_VARIANT, registriesFuture);
  }

  @Override
  protected void configure(WrapperLookup registries) {
    this.builder(ZMobVariantTags.ZOMBIE_VARIANTS).add(ZombieVariantGenerator.COMMON);
  }
}
