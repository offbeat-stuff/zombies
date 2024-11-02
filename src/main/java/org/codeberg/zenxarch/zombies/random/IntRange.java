package org.codeberg.zenxarch.zombies.random;

import com.google.common.collect.AbstractIterator;
import net.minecraft.util.math.random.Random;

public record IntRange(int min, int max) {

  public static IntRange of(int min, int max) {
    return new IntRange(min, max);
  }

  public static IntRange around(int center, int range) {
    return new IntRange(center - range, center + range);
  }

  public boolean isValid() {
    return min <= max;
  }

  public IntRange union(IntRange other) {
    return of(Math.min(min, other.min), Math.max(max, other.max));
  }

  public IntRange intersection(IntRange other) {
    return of(Math.max(min, other.min), Math.min(max, other.max));
  }

  public int next(Random random) {
    if (!isValid()) return min;
    return random.nextBetween(min, max);
  }

  public IntRange shiftBy(int shift) {
    return of(min + shift, max + shift);
  }

  public boolean contains(int value) {
    return value >= min && value <= max;
  }

  public Iterable<Integer> iterate() {
    return () ->
        new AbstractIterator<Integer>() {
          int value = min;

          protected Integer computeNext() {
            if (value > max) return this.endOfData();
            value++;
            return value - 1;
          }
        };
  }
}
