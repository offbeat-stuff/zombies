package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.biome.Biome;

public record BiomePredicate(List<TagSetEntry> tags) implements Predicate<RegistryEntry<Biome>> {

  public static final BiomePredicate DEFAULT = new BiomePredicate(List.of());

  public static final Codec<BiomePredicate> CODEC =
      Codec.withAlternative(
          TagKey.codec(RegistryKeys.BIOME)
              .flatComapMap(
                  tag -> new BiomePredicate(List.of(new TagSetEntry(true, tag))),
                  pred ->
                      pred.tags.size() == 1 && pred.tags.getFirst().spawnIn
                          ? DataResult.success(pred.tags.getFirst().biomeTag)
                          : DataResult.error(() -> "cannot be a string")),
          RecordCodecBuilder.create(
              instance ->
                  instance
                      .group(
                          TagSetEntry.CODEC
                              .listOf()
                              .optionalFieldOf("tags", List.of())
                              .forGetter(BiomePredicate::tags))
                      .apply(instance, BiomePredicate::new)));

  static record TagSetEntry(boolean spawnIn, TagKey<Biome> biomeTag)
      implements Predicate<RegistryEntry<Biome>> {

    public static final Codec<TagSetEntry> CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        Codec.BOOL.optionalFieldOf("spawnIn", true).forGetter(TagSetEntry::spawnIn),
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
