package org.codeberg.zenxarch.zombies.helper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public record WeightedRegistryEntryPredicate<T>(
    RegistryKey<? extends Registry<T>> registry,
    List<RegistryEntryPredicate<T>> list,
    double min,
    double max) {
  public static <T> WeightedRegistryEntryPredicate<T> fromString(
      RegistryKey<? extends Registry<T>> registry, String value) {
    var p = value.split("<");
    return switch (p.length) {
      case 1 -> helperNew(Optional.empty(), registry, value, Optional.empty());
      case 2 ->
          parseDouble(p[0])
              .map(v -> helperNew(Optional.of(v), registry, p[1], Optional.empty()))
              .orElseGet(() -> helperNew(Optional.empty(), registry, p[0], parseDouble(p[1])));
      case 3 -> helperNew(parseDouble(p[0]), registry, p[1], parseDouble(p[2]));
      default -> new WeightedRegistryEntryPredicate<T>(registry, List.of(), 1.0, 1.0);
    };
  }

  @Override
  public final String toString() {
    var builder = new StringBuilder();
    if (this.min != 0.0) builder.append(this.min).append("<");
    var list = this.list.stream().map(v -> v.toString()).toList();
    builder.append(String.join(",", list));
    if (this.max != 1.0) builder.append("<").append(this.max);
    return builder.toString();
  }

  private static <T> WeightedRegistryEntryPredicate<T> helperNew(
      Optional<Double> min,
      RegistryKey<? extends Registry<T>> registry,
      String list,
      Optional<Double> max) {
    return new WeightedRegistryEntryPredicate<T>(
        registry, parseList(registry, list), min.orElse(0.0), max.orElse(1.0));
  }

  private static Optional<Double> parseDouble(String value) {
    try {
      return Optional.of(Double.parseDouble(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static <T> List<RegistryEntryPredicate<T>> parseList(
      RegistryKey<? extends Registry<T>> registry, String value) {
    return List.of(value.split(",")).stream()
        .map(v -> new RegistryEntryPredicate<>(registry, v))
        .filter(v -> !(v.value() instanceof OptionalPair.Empty))
        .toList();
  }

  public boolean matches(RegistryEntry<T> entry) {
    for (var v : this.list) if (v.test(entry)) return true;
    return false;
  }

  public boolean nextBoolean(Random random, double progress) {
    var chance = MathHelper.lerp(progress, this.min, this.max);
    return ProbabilityImpl.nextBoolean(random, chance);
  }

  public Stream<RegistryEntry<T>> streamEntries(Registry<T> registry, double progress) {
    var chance = MathHelper.lerp(progress, this.min, this.max);
    if (chance < 0.0) return Stream.empty();
    Stream<RegistryEntry<T>> result = Stream.empty();
    for (var v : this.list) result = Stream.concat(result, v.streamEntries(registry));
    return result;
  }
}
