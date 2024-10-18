package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.EQUIPMENT_CONFIG;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.helper.ItemSelector;
import org.codeberg.zenxarch.zombies.helper.LerpImpl;

public abstract class Equipment {
  // private static final Comparator<Double> LOOSE_COMPARE =
  // (a, b) -> Math.abs(b - a) < 0.4 ? 0 : Double.compare(a, b);

  private static final List<Double> ARMOR_SELECT_CHANCE = List.of(0.9, 0.3);

  private static ItemGenerator weightedSelect(
      List<List<TagKey<Item>>> tags,
      EquipmentSlot slot,
      List<ItemSelector> selectors,
      Random random,
      double min,
      double max) {
    var selected = LerpImpl.lerpWeighted(random, min, max, tags.size());
    return ItemGenerator.fromBuilder(
        b -> {
          var tagList = tags.get(selected);
          for (var tag : tagList) b.add(tag);
        },
        v -> {},
        slot,
        selectors.get(selected));
  }

  public static Item getItemForSlot(EquipmentSlot slot, Random random, double difficulty) {
    var armorSelector =
        new ItemSelector.RecursiveChanceBased((delta) -> LerpImpl.lerp(ARMOR_SELECT_CHANCE, delta));
    var basicWeaponSelector =
        new ItemSelector.WeightedSelector((d) -> LerpImpl.lerp(List.of(1000.0), 100.0), (d) -> 1.0);
    var generator =
        switch (slot) {
          case HEAD -> ItemGenerator.fromTag(ItemTags.HEAD_ARMOR_ENCHANTABLE, slot, armorSelector);
          case CHEST ->
              ItemGenerator.fromTag(ItemTags.CHEST_ARMOR_ENCHANTABLE, slot, armorSelector);
          case LEGS -> ItemGenerator.fromTag(ItemTags.LEG_ARMOR_ENCHANTABLE, slot, armorSelector);
          case FEET -> ItemGenerator.fromTag(ItemTags.LEG_ARMOR_ENCHANTABLE, slot, armorSelector);
          case MAINHAND ->
              weightedSelect(
                  List.of(
                      List.of(ItemTags.SWORDS),
                      List.of(ItemTags.AXES),
                      List.of(ItemTags.TRIDENT_ENCHANTABLE, ItemTags.MACE_ENCHANTABLE)),
                  slot,
                  List.of(
                      basicWeaponSelector,
                      basicWeaponSelector,
                      new ItemSelector.RecursiveChanceBased((d) -> 0.3)),
                  random,
                  250.0,
                  1.0);
          case OFFHAND ->
              ItemGenerator.fromItem(
                  Items.SHIELD, slot, new ItemSelector.RecursiveChanceBased((d) -> 0.3));
          default -> null;
        };
    if (generator == null) return null;
    return generator.generate(random, difficulty);
  }

  public static Optional<Item> getEquipmentForSlot(
      Random random, double difficulty, EquipmentSlot slot) {
    var item =
        ExtendedDifficulty.shouldSpawnWithEquipment(difficulty)
            ? Equipment.getItemForSlot(slot, random, difficulty)
            : null;
    return Optional.ofNullable(item);
  }

  public static ItemStack enchant(
      ServerWorldAccess world, Random random, double difficulty, ItemStack input) {
    var level = ExtendedDifficulty.getEnchantLevel(difficulty);
    if (level == 0) return input;

    var enchantments = EQUIPMENT_CONFIG.getEnchantments(world, difficulty);
    return EnchantmentHelper.enchant(random, input, level, enchantments);
  }
}
