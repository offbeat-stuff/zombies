package org.codeberg.zenxarch.zombies.helper;

import java.util.Optional;
import java.util.function.Function;

public sealed interface OptionalPair<K, V>
    permits OptionalPair.Left, OptionalPair.Right, OptionalPair.Empty {

  public static <K, V> OptionalPair<K, V> left(K element) {
    return element == null ? empty() : new Left<K, V>(element);
  }

  public static <K, V> OptionalPair<K, V> right(V element) {
    return element == null ? empty() : new Right<K, V>(element);
  }

  public static <K, V> OptionalPair<K, V> empty() {
    return new Empty<K, V>();
  }

  public default <L, R> OptionalPair<L, R> map(Function<K, L> l, Function<V, R> r) {
    return switch (this) {
      case Left(var v) -> left(l.apply(v));
      case Right(var v) -> right(r.apply(v));
      case Empty() -> empty();
    };
  }

  public default <T> Optional<T> mapAndUnwrap(Function<K, T> l, Function<V, T> r) {
    return Optional.ofNullable(
        switch (this) {
          case Left(var v) -> l.apply(v);
          case Right(var v) -> r.apply(v);
          case Empty() -> null;
        });
  }

  public default Optional<K> mapToLeft(Function<V, K> r) {
    return mapAndUnwrap(a -> a, r);
  }

  public default Optional<V> mapToRight(Function<K, V> l) {
    return mapAndUnwrap(l, a -> a);
  }

  public static <U> U unwrap(OptionalPair<? extends U, ? extends U> pair) {
    return switch (pair) {
      case Left(var v) -> v;
      case Right(var v) -> v;
      case Empty() -> null;
    };
  }

  public K getLeft();

  public V getRight();

  public static final record Left<K, V>(K element) implements OptionalPair<K, V> {
    @Override
    public K getLeft() {
      return element;
    }

    @Override
    public V getRight() {
      return null;
    }
  }

  public static final record Right<K, V>(V element) implements OptionalPair<K, V> {
    @Override
    public K getLeft() {
      return null;
    }

    @Override
    public V getRight() {
      return element;
    }
  }

  public static final record Empty<K, V>() implements OptionalPair<K, V> {
    @Override
    public K getLeft() {
      return null;
    }

    @Override
    public V getRight() {
      return null;
    }
  }
}
