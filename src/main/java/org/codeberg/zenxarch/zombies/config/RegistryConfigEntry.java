package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import org.codeberg.zenxarch.zombies.registry.TagEntry;

public record RegistryConfigEntry(TagEntry value) implements ConfigSerializableObject<String> {

  @Override
  public ComplexConfigValue copy() {
    return new RegistryConfigEntry(this.value);
  }

  @Override
  public ConfigSerializableObject<String> convertFrom(String value) {
    return new RegistryConfigEntry(TagEntry.fromString(value).orElse(new TagEntry(false, null)));
  }

  @Override
  public String getRepresentation() {
    return this.value.toString();
  }
}
