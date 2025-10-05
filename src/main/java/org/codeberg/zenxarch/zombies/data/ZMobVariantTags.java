package org.codeberg.zenxarch.zombies.data;

import net.minecraft.registry.tag.TagKey;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;
import org.codeberg.zenxarch.mob_variants_api.variant.MobVariant;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZMobVariantTags {
  public static final TagKey<MobVariant> ZOMBIE_VARIANTS = of("zombie_variants");

  private static TagKey<MobVariant> of(String id) {
    return TagKey.of(MobRegistryKeys.MOB_VARIANT, Zombies.id(id));
  }
}
