package org.codeberg.zenxarch.zombies.helper;

public sealed interface OptionalPair<K, V>
    permits OptionalPair.Left, OptionalPair.Right, OptionalPair.Empty {

  public static <K, V> OptionalPair<K, V> left(K element) {
    return new Left<K, V>(element);
  }

  public static <K, V> OptionalPair<K, V> right(V element) {
    return new Right<K, V>(element);
  }

  public static <K, V> OptionalPair<K, V> empty() {
    return new Empty<K, V>();
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
