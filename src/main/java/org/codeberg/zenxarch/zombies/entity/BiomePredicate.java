package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public record BiomePredicate(List<TagSetEntry> tags) implements Predicate<RegistryEntry<Biome>> {

  public static final MapCodec<BiomePredicate> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(TagSetEntry.CODEC.listOf().fieldOf("tags").forGetter(BiomePredicate::tags))
                  .apply(instance, BiomePredicate::new));

  static record TagSetEntry(boolean spawnIn, TagKey<Biome> biomeTag)
      implements Predicate<RegistryEntry<Biome>> {

    public static final Codec<TagSetEntry> CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        Codec.BOOL.fieldOf("spawnIn").forGetter(TagSetEntry::spawnIn),
                        TagKey.codec(RegistryKeys.BIOME)
                            .fieldOf("biomeTag")
                            .forGetter(TagSetEntry::biomeTag))
                    .apply(instance, TagSetEntry::new));

    @Override
    public boolean test(RegistryEntry<Biome> biome) {
      return spawnIn ? biome.isIn(biomeTag) : !biome.isIn(biomeTag);
    }
  }

  @Override
  public boolean test(RegistryEntry<Biome> biome) {
    for (var tagEntry : tags) {
      if (!tagEntry.test(biome)) return false;
    }
    return true;
  }
  ;
}
