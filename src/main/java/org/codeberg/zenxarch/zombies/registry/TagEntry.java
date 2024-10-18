package org.codeberg.zenxarch.zombies.registry;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList.Named;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record TagEntry(boolean isTag, Identifier id) {

  public static TagEntry EMPTY = new TagEntry(false, null);

  public static TagEntry of(TagKey<?> tag) {
    return new TagEntry(true, tag.id());
  }

  public static TagEntry of(RegistryKey<?> key) {
    return new TagEntry(true, key.getValue());
  }

  public String toString() {
    if (id == null) return "";
    var stringBuilder = new StringBuilder();
    if (isTag) stringBuilder.append("#");
    stringBuilder.append(id.toString());
    return stringBuilder.toString();
  }

  public static Optional<TagEntry> fromString(String value) {
    var isTag = value.startsWith("#");
    if (isTag) value = value.substring(1);
    var id = Identifier.tryParse(value);
    if (id == null) return Optional.empty();
    return Optional.of(new TagEntry(isTag, id));
  }

  public <T> boolean matches(RegistryEntry<T> entry) {
    return isTag ? entry.streamTags().anyMatch(v -> v.id().equals(id)) : entry.matchesId(id);
  }

  public <T> Stream<? extends RegistryEntry<T>> streamEntries(Registry<T> registry) {
    return isTag
        ? registry.getEntryList(TagKey.of(registry.getKey(), id)).stream().flatMap(Named::stream)
        : registry.getEntry(id).stream();
  }
}
