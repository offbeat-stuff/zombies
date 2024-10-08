package org.codeberg.zenxarch.zombies.helper;

import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public record RegistryEntryPredicate<T>(
    RegistryKey<? extends Registry<T>> registry, OptionalPair<TagKey<T>, RegistryKey<T>> value)
    implements Predicate<RegistryEntry<T>> {

  public RegistryEntryPredicate(RegistryKey<? extends Registry<T>> registry, String value) {
    this(registry, parse(registry, value));
  }

  public static <T> RegistryEntryPredicate<T> registry(
      RegistryKey<? extends Registry<T>> registry) {
    return new RegistryEntryPredicate<T>(registry, OptionalPair.empty());
  }

  public static <T> RegistryEntryPredicate<T> tag(TagKey<T> tag) {
    return new RegistryEntryPredicate<T>(tag.registry(), OptionalPair.left(tag));
  }

  public static <T> RegistryEntryPredicate<T> key(RegistryKey<T> key) {
    return new RegistryEntryPredicate<T>(key.getRegistryRef(), OptionalPair.right(key));
  }

  private static <T> OptionalPair<TagKey<T>, RegistryKey<T>> parse(
      RegistryKey<? extends Registry<T>> registry, String value) {
    value = value.strip();
    var isTag = value.startsWith("#");
    var id = Identifier.of(filter(value));
    if (id.getPath().isEmpty()) return OptionalPair.empty();
    return isTag
        ? OptionalPair.left(TagKey.of(registry, id))
        : OptionalPair.right(RegistryKey.of(registry, id));
  }

  private static String filter(String value) {
    return value
        .toLowerCase()
        .codePoints()
        .filter((c) -> Identifier.isCharValid((char) c))
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }

  @Override
  public final String toString() {
    return switch (this.value) {
      case OptionalPair.Left(var left) -> "#" + left.id().toString();
      case OptionalPair.Right(var right) -> right.getValue().toString();
      case OptionalPair.Empty() -> "";
    };
  }

  @Override
  public boolean test(RegistryEntry<T> entry) {
    return switch (this.value) {
      case OptionalPair.Left(var left) -> entry.isIn(left);
      case OptionalPair.Right(var right) -> entry.matchesKey(right);
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
