package org.codeberg.zenxarch.zombies.helper;

import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public abstract class RegistryHandler {
  public static <T> OptionalPair<TagKey<T>, RegistryKey<T>> tagOrKeyFromString(
      RegistryKey<? extends Registry<T>> registry, String value) {
    value = value.strip();
    var isTag = value.startsWith("#");
    var id = Identifier.of(filter(value));
    if (id.getPath().isEmpty()) return OptionalPair.empty();
    return isTag
        ? OptionalPair.first(TagKey.of(registry, id))
        : OptionalPair.second(RegistryKey.of(registry, id));
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
