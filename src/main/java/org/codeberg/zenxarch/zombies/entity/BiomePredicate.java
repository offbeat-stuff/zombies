package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.Biome;

public record BiomePredicate(List<TagSetEntry> tags) implements Predicate<RegistryEntry<Biome>> {

  public static final BiomePredicate DEFAULT = new BiomePredicate(List.of());

  public static final Codec<BiomePredicate> CODEC =
      Codecs.listOrSingle(TagSetEntry.CODEC).xmap(BiomePredicate::new, BiomePredicate::tags);

  static record TagSetEntry(boolean spawnIn, TagKey<Biome> biomeTag)
      implements Predicate<RegistryEntry<Biome>> {

    public static final Codec<TagSetEntry> CODEC =
        Codec.STRING.comapFlatMap(
            TagSetEntry::fromString, entry -> (entry.spawnIn ? "" : "!") + entry.biomeTag.id());

    @Override
    public boolean test(RegistryEntry<Biome> biome) {
      return spawnIn ? biome.isIn(biomeTag) : !biome.isIn(biomeTag);
    }

    private static DataResult<TagSetEntry> fromString(String value) {
      value = value.strip();
      final var spawnIn = !value.startsWith("!");
      if (!spawnIn) value = value.substring(1);
      value = value.strip();
      if (value.startsWith("#")) value = value.substring(1);
      return Identifier.validate(value)
          .map(id -> new TagSetEntry(spawnIn, TagKey.of(RegistryKeys.BIOME, id)));
    }
  }

  @Override
  public boolean test(RegistryEntry<Biome> biome) {
    for (var tagEntry : tags) {
      if (!tagEntry.test(biome)) return false;
    }
    return true;
  }
}
