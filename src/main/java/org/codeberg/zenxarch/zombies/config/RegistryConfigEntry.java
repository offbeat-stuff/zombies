package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import java.util.function.Predicate;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import org.codeberg.zenxarch.zombies.helper.OptionalPair;
import org.codeberg.zenxarch.zombies.helper.RegistryHandler;

public class RegistryConfigEntry<T>
    implements ConfigSerializableObject<String>, Predicate<RegistryEntry<T>> {

  private OptionalPair<TagKey<T>, RegistryKey<T>> value = OptionalPair.empty();
  private RegistryKey<? extends Registry<T>> registry;

  public RegistryConfigEntry<T> withTag(TagKey<T> tag) {
    if (this.value.isSecond()) return this;
    this.value = OptionalPair.first(tag);
    this.registry = tag.registry();
    return this;
  }

  public RegistryConfigEntry<T> withKey(RegistryKey<T> key) {
    if (this.value.isFirst()) return this;
    this.value = OptionalPair.second(key);
    this.registry = key.getRegistryRef();
    return this;
  }

  /** Cause of reasons had to */
  public RegistryConfigEntry(RegistryKey<? extends Registry<T>> registry) {
    this.registry = registry;
  }

  public RegistryConfigEntry<T> withValue(OptionalPair<TagKey<T>, RegistryKey<T>> value) {
    this.value = value;
    return this;
  }

  @Override
  public ComplexConfigValue copy() {
    return new RegistryConfigEntry<T>(this.registry).withValue(value);
  }

  @Override
  public ConfigSerializableObject<String> convertFrom(String value) {
    return new RegistryConfigEntry<T>(this.registry)
        .withValue(RegistryHandler.tagOrKeyFromString(this.registry, value));
  }

  @Override
  public String getRepresentation() {
    if (this.value.isFirst()) return "#" + this.value.getFirst().id().toString();
    if (this.value.isSecond()) return this.value.getSecond().getValue().toString();
    return "";
  }

  public boolean test(RegistryEntry<T> value) {
    if (this.value.isFirst()) return value.isIn(this.value.getFirst());
    if (this.value.isSecond()) return value.matchesKey(this.value.getSecond());
    return false;
  }
}
