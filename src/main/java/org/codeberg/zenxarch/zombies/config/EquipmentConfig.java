package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import folk.sisby.kaleido.lib.quiltconfig.api.values.ValueList;
import java.util.stream.Stream;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.world.ServerWorldAccess;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class EquipmentConfig extends ReflectiveConfig {

  @Comment("This is a list of enchantments")
  @Comment("The enchants can either be tags or id")
  public final TrackedValue<ValueList<RegistryConfigEntry>> ENCHANTMENTS =
      this.value(
          ConfigUtils.tagList(builder -> builder.add(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT)));

  public Stream<RegistryEntry<Enchantment>> getEnchantments(ServerWorldAccess world) {
    var registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    return this.ENCHANTMENTS.value().stream().flatMap(v -> v.value().streamEntries(registry));
  }
}
