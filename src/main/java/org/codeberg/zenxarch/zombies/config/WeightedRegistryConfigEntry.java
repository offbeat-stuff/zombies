package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import org.codeberg.zenxarch.zombies.helper.WeightedRegistryEntryPredicate;

public record WeightedRegistryConfigEntry<T>(WeightedRegistryEntryPredicate<T> value)
    implements ConfigSerializableObject<String> {

  @Override
  public ComplexConfigValue copy() {
    return new WeightedRegistryConfigEntry<T>(this.value);
  }

  @Override
  public ConfigSerializableObject<String> convertFrom(String value) {
    return new WeightedRegistryConfigEntry<T>(
        WeightedRegistryEntryPredicate.fromString(this.value.registry(), value));
  }

  @Override
  public String getRepresentation() {
    return this.value.toString();
  }
}
