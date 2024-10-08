package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import java.util.function.Predicate;
import java.util.stream.Stream;
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
    if (!(this.value instanceof OptionalPair.Right)) {
      this.value = OptionalPair.left(tag);
      this.registry = tag.registry();
    }
    return this;
  }

  public RegistryConfigEntry<T> withKey(RegistryKey<T> key) {
    if (!(this.value instanceof OptionalPair.Left)) {
      this.value = OptionalPair.right(key);
      this.registry = key.getRegistryRef();
    }
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
    return switch (this.value) {
      case OptionalPair.Left(var left) -> "#" + left.id().toString();
      case OptionalPair.Right(var right) -> right.getValue().toString();
      case OptionalPair.Empty() -> "";
    };
  }

  public boolean test(RegistryEntry<T> value) {
    return switch (this.value) {
      case OptionalPair.Left(var left) -> value.isIn(left);
      case OptionalPair.Right(var right) -> value.matchesKey(right);
      case OptionalPair.Empty() -> false;
    };
  }

  public Stream<? extends RegistryEntry<T>> streamEntries(Registry<T> registry) {
    return switch (this.value) {
      case OptionalPair.Left(var left) -> registry.getOrCreateEntryList(left).stream();
      case OptionalPair.Right(var right) -> registry.getEntry(right).stream();
      case OptionalPair.Empty() -> Stream.empty();
    };
  }
}
