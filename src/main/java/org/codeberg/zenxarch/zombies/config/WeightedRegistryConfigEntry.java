package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import org.codeberg.zenxarch.zombies.helper.WeightedRegistryEntries;

public record WeightedRegistryConfigEntry(WeightedRegistryEntries value)
    implements ConfigSerializableObject<String> {

  @Override
  public ComplexConfigValue copy() {
    return new WeightedRegistryConfigEntry(this.value);
  }

  @Override
  public ConfigSerializableObject<String> convertFrom(String value) {
    return new WeightedRegistryConfigEntry(WeightedRegistryEntries.fromString(value));
  }

  @Override
  public String getRepresentation() {
    return this.value.toString();
  }
}
