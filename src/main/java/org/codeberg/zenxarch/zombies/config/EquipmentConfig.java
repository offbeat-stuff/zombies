package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.helper.WeightedRegistryEntries;
import org.codeberg.zenxarch.zombies.registry.TagEntryListBuilder;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class EquipmentConfig extends ReflectiveConfig {

  @Comment("This is a list of enchantments with their respective chances of being applies")
  @Comment("The sytax for this is min<enchant1,enchant2,enchant3<max")
  @Comment("The min defaults to 0.0 and max defaults to 1.0")
  @Comment("The min and max will be interpolated using difficulty value (defined in time config)")
  @Comment(
      "If the value comes out above 0.0 the enchantment list is added to possible pool of enchants")
  @Comment("The enchants can either be tags or id")
  public final TrackedValue<ValueList<WeightedRegistryConfigEntry>> ENCHANTMENTS =
      this.list(
          helperNew(0.0, 1.0, b -> {}),
          helperNew(0.0, 1.0, builder -> builder.add(EnchantmentTags.NON_TREASURE)),
          helperNew(-1.0, 0.25, builder -> builder.add(EnchantmentTags.TREASURE)));

  private static WeightedRegistryConfigEntry helperNew(
      double min, double max, Consumer<TagEntryListBuilder> buildFunction) {
    var builder = new TagEntryListBuilder();
    buildFunction.accept(builder);
    return new WeightedRegistryConfigEntry(new WeightedRegistryEntries(builder.build(), min, max));
  }

  public Stream<RegistryEntry<Enchantment>> getEnchantments(
      ServerWorldAccess world, double difficulty) {
    var registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    return this.ENCHANTMENTS.value().stream()
        .flatMap(v -> v.value().streamEntries(registry, difficulty));
  }
}
