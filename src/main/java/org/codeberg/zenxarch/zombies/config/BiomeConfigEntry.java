package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;

public class BiomeConfigEntry
    implements ConfigSerializableObject<String>, Predicate<RegistryEntry<Biome>> {

  private Optional<TagKey<Biome>> tag = Optional.empty();
  private Optional<RegistryKey<Biome>> biome = Optional.empty();

  public BiomeConfigEntry(TagKey<Biome> tag) {
    this.tag = Optional.of(tag);
  }

  public BiomeConfigEntry(RegistryKey<Biome> biome) {
    this.biome = Optional.of(biome);
  }

  public BiomeConfigEntry() {}

  @Override
  public ComplexConfigValue copy() {
    if (this.tag.isPresent()) return new BiomeConfigEntry(this.tag.get());
    if (this.biome.isPresent()) return new BiomeConfigEntry(this.biome.get());
    return new BiomeConfigEntry();
  }

  @Override
  public ConfigSerializableObject<String> convertFrom(String value) {
    value = value.strip();
    var isTag = value.startsWith("#");
    var id = Identifier.of(filter(value));
    if (id.getPath().isEmpty()) return new BiomeConfigEntry();
    return (isTag)
        ? new BiomeConfigEntry(TagKey.of(RegistryKeys.BIOME, id))
        : new BiomeConfigEntry(RegistryKey.of(RegistryKeys.BIOME, id));
  }

  @Override
  public String getRepresentation() {
    if (this.tag.isPresent()) return "#" + this.tag.get().id().toString();
    if (this.biome.isPresent()) return this.biome.get().toString();
    return "";
  }

  public boolean test(RegistryEntry<Biome> value) {
    if (this.tag.isPresent()) return value.isIn(this.tag.get());
    if (this.biome.isPresent()) return value.matchesKey(this.biome.get());
    return false;
  }

  private static String filter(String value) {
    return value
        .toLowerCase()
        .codePoints()
        .filter((c) -> Identifier.isCharValid((char) c))
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
