package org.codeberg.zenxarch.zombies.helper;

import java.util.Objects;
import java.util.Optional;

public class OptionalPair<K, V> {
  Optional<K> first;
  Optional<V> second;

  private OptionalPair(K first, V second) {
    this.first = Optional.ofNullable(first);
    this.second = Optional.ofNullable(second);
  }

  public static <K, V> OptionalPair<K, V> first(K first) {
    return new OptionalPair<K, V>(Objects.requireNonNull(first), null);
  }

  public static <K, V> OptionalPair<K, V> second(V second) {
    return new OptionalPair<K, V>(null, Objects.requireNonNull(second));
  }

  public static <K, V> OptionalPair<K, V> empty() {
    return new OptionalPair<K, V>(null, null);
  }

  public K getFirst() {
    return this.first.get();
  }

  public V getSecond() {
    return this.second.get();
  }

  public boolean isFirst() {
    return first.isPresent();
  }

  public boolean isSecond() {
    return second.isPresent();
  }

  public boolean isEmpty() {
    return first.isEmpty() && second.isEmpty();
  }
}
