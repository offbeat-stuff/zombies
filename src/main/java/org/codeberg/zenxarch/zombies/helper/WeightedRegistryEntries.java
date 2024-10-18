package org.codeberg.zenxarch.zombies.helper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.codeberg.zenxarch.zombies.registry.TagEntry;

public record WeightedRegistryEntries(List<TagEntry> list, double min, double max) {
  public static WeightedRegistryEntries fromString(String value) {
    var p = value.split("<");
    return switch (p.length) {
      case 1 -> helperNew(Optional.empty(), value, Optional.empty());
      case 2 ->
          parseDouble(p[0])
              .map(v -> helperNew(Optional.of(v), p[1], Optional.empty()))
              .orElseGet(() -> helperNew(Optional.empty(), p[0], parseDouble(p[1])));
      case 3 -> helperNew(parseDouble(p[0]), p[1], parseDouble(p[2]));
      default -> new WeightedRegistryEntries(List.of(), 1.0, 1.0);
    };
  }

  @Override
  public final String toString() {
    var builder = new StringBuilder();
    if (this.min != 0.0) builder.append(this.min).append("<");
    for (var v : this.list) builder.append(v).append(",");
    builder.setLength(builder.length() - 1);
    if (this.max != 1.0) builder.append("<").append(this.max);
    return builder.toString();
  }

  private static WeightedRegistryEntries helperNew(
      Optional<Double> min, String list, Optional<Double> max) {
    return new WeightedRegistryEntries(parseList(list), min.orElse(0.0), max.orElse(1.0));
  }

  private static Optional<Double> parseDouble(String value) {
    try {
      return Optional.of(Double.parseDouble(value));
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  private static List<TagEntry> parseList(String value) {
    return List.of(value.split(",")).stream()
        .map(TagEntry::fromString)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  public <T> boolean matches(RegistryEntry<T> entry) {
    return this.list.stream().anyMatch(v -> v.matches(entry));
  }

  public boolean nextBoolean(Random random, double progress) {
    var chance = MathHelper.lerp(progress, this.min, this.max);
    return ProbabilityImpl.nextBoolean(random, chance);
  }

  public <T> Stream<RegistryEntry<T>> streamEntries(Registry<T> registry, double progress) {
    var chance = MathHelper.lerp(progress, this.min, this.max);
    if (chance < 0.0) return Stream.empty();
    return this.list.stream().flatMap(v -> v.streamEntries(registry));
  }
}
