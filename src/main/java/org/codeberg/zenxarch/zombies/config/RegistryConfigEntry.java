package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ComplexConfigValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ConfigSerializableObject;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import org.codeberg.zenxarch.zombies.helper.RegistryEntryPredicate;

public record RegistryConfigEntry<T>(RegistryEntryPredicate<T> value)
    implements ConfigSerializableObject<String>, Predicate<RegistryEntry<T>> {

  public static <T> RegistryConfigEntry<T> tag(TagKey<T> tag) {
    return new RegistryConfigEntry<>(RegistryEntryPredicate.tag(tag));
  }

  public static <T> RegistryConfigEntry<T> key(RegistryKey<T> key) {
    return new RegistryConfigEntry<>(RegistryEntryPredicate.key(key));
  }

  public static <T> RegistryConfigEntry<T> registry(RegistryKey<? extends Registry<T>> registry) {
    return new RegistryConfigEntry<>(RegistryEntryPredicate.registry(registry));
  }

  @Override
  public ComplexConfigValue copy() {
    return new RegistryConfigEntry<T>(this.value);
  }

  @Override
  public ConfigSerializableObject<String> convertFrom(String value) {
    return new RegistryConfigEntry<T>(new RegistryEntryPredicate<>(this.value.registry(), value));
  }

  @Override
  public String getRepresentation() {
    return this.value.toString();
  }

  public boolean test(RegistryEntry<T> value) {
    return this.value.test(value);
  }

  public Stream<? extends RegistryEntry<T>> streamEntries(Registry<T> registry) {
    return this.value.streamEntries(registry);
  }
}
