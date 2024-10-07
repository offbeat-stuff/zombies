package org.codeberg.zenxarch.zombies.config;

import static org.codeberg.zenxarch.zombies.config.RegistryEntries.*;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ServerWorldAccess;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class EquipmentConfig extends ReflectiveConfig {

  public final EnchantmentList NON_TREASURE =
      new EnchantmentList(1.0, 1.0, EnchantmentTags.NON_TREASURE);
  public final EnchantmentList TREASURE = new EnchantmentList(-1.0, 0.25, EnchantmentTags.TREASURE);

  public static class EnchantmentList extends Section {
    public final TrackedValue<Double> min;
    public final TrackedValue<Double> max;
    public final TrackedValue<ValueList<RegistryConfigEntry<Enchantment>>> entries;

    public EnchantmentList(double min, double max, TagKey<Enchantment> tag) {
      this.min = this.value(min);
      this.max = this.value(max);
      this.entries = this.list(enchantmentEntry(), enchantmentEntry(tag));
    }

    public Stream<RegistryEntry<Enchantment>> getEnchantments(
        ServerWorldAccess world, double difficulty) {
      Stream<RegistryEntry<Enchantment>> stream = Stream.empty();
      var registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
      if (MathHelper.lerp(difficulty, this.min.value(), this.max.value()) < 0.0) return stream;
      for (var entry : this.entries.value())
        stream = Stream.concat(stream, entry.streamEntries(registry));
      return stream;
    }
  }
}
