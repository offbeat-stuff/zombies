package org.codeberg.zenxarch.zombies.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

public class TagBuilder {
  private final List<TagEntry> entries = new ArrayList<TagEntry>();

  public List<TagEntry> build() {
    return List.copyOf(entries);
  }

  public TagBuilder add(TagEntry tagEntry) {
    this.entries.add(tagEntry);
    return this;
  }

  public <T> TagBuilder add(RegistryKey<T> key) {
    return this.add(TagEntry.of(key));
  }

  @SafeVarargs
  public final <T> TagBuilder add(RegistryKey<T>... keys) {
    for (var key : keys) this.add(key);
    return this;
  }

  public <T> TagBuilder add(TagKey<T> tag) {
    return this.add(TagEntry.of(tag));
  }

  @SafeVarargs
  public final <T> TagBuilder add(TagKey<T>... tags) {
    for (var tag : tags) this.add(tag);
    return this;
  }

  public final <T> TagBuilder add(
      Registry<T> registry, Consumer<InnerTagBuilder<T>> buildFunction) {
    buildFunction.accept(new InnerTagBuilder<>(registry, this));
    return this;
  }

  public static record InnerTagBuilder<T>(Registry<T> registry, TagBuilder builder) {
    public final InnerTagBuilder<T> add(T value) {
      registry.getKey(value).ifPresent(builder::add);
      return this;
    }

    @SafeVarargs
    public final InnerTagBuilder<T> add(T... values) {
      for (var value : values) this.add(value);
      return this;
    }
  }
}
