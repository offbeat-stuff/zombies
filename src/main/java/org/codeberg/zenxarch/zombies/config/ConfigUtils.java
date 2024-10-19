package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import java.util.function.Consumer;
import org.codeberg.zenxarch.zombies.registry.TagBuilder;
import org.codeberg.zenxarch.zombies.registry.TagEntry;

public abstract class ConfigUtils {
  public static ValueList<RegistryConfigEntry> tagList(Consumer<TagBuilder> buildFunc) {
    var builder = new TagBuilder();
    buildFunc.accept(builder);
    return ValueList.create(
        new RegistryConfigEntry(TagEntry.EMPTY),
        (RegistryConfigEntry[]) builder.build().stream().map(RegistryConfigEntry::new).toArray());
  }
}
