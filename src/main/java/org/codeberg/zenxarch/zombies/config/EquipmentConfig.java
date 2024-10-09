package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
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

  public final TrackedValue<ValueList<WeightedRegistryConfigEntry<Enchantment>>> ENCHANTMENTS =
      this.list(
          helperNew(0.0, 1.0, List.of()),
          helperNew(1.0, 1.0, List.of(EnchantmentTags.NON_TREASURE)),
          helperNew(-1.0, 0.25, List.of(EnchantmentTags.TREASURE)));

  private static WeightedRegistryConfigEntry<Enchantment> helperNew(
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
