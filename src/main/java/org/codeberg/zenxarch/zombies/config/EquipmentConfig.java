package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.helper.RegistryEntryPredicate;
import org.codeberg.zenxarch.zombies.helper.WeightedRegistryEntryPredicate;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class EquipmentConfig extends ReflectiveConfig {

  @Comment("This is a list of enchantments with their respective chances of being applies")
  @Comment("The sytax for this is min<enchant1,enchant2,enchant3<max")
  @Comment("The min defaults to 0.0 and max defaults to 1.0")
  @Comment("The min and max will be interpolated using difficulty value (defined in time config)")
  @Comment(
      "If the value comes out above 0.0 the enchantment list is added to possible pool of enchants")
  @Comment("The enchants can either be tags or id")
  public final TrackedValue<ValueList<WeightedRegistryConfigEntry<Enchantment>>> ENCHANTMENTS =
      this.list(
          newEnchantmentEntry(0.0, 1.0, List.of()),
          newEnchantmentEntry(0.0, 1.0, List.of(EnchantmentTags.NON_TREASURE)),
          newEnchantmentEntry(-1.0, 0.25, List.of(EnchantmentTags.TREASURE)));

  private static WeightedRegistryConfigEntry<Enchantment> newEnchantmentEntry(
      double min, double max, List<TagKey<Enchantment>> tag) {
    return new WeightedRegistryConfigEntry<Enchantment>(
        new WeightedRegistryEntryPredicate<Enchantment>(
            RegistryKeys.ENCHANTMENT,
            tag.stream().map(RegistryEntryPredicate::tag).toList(),
            min,
            max));
  }

  public Stream<RegistryEntry<Enchantment>> getEnchantments(
      ServerWorldAccess world, double difficulty) {
    Stream<RegistryEntry<Enchantment>> result = Stream.empty();
    var registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    for (var v : this.ENCHANTMENTS.value())
      result = Stream.concat(result, v.value().streamEntries(registry, difficulty));
    return result;
  }
}
