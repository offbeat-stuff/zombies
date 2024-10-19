package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.data.ItemSelector;
import org.codeberg.zenxarch.zombies.random.LerpUtils;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

public abstract class Equipment {
  private static final List<Double> ARMOR_SELECT_CHANCE = List.of(0.9, 0.3);

  public static Item getItemForSlot(EquipmentSlot slot, Random random, double difficulty) {
    var armorSelector =
        new ItemSelector.RecursiveChanceBased(
            (delta) -> LerpUtils.lerp(ARMOR_SELECT_CHANCE, delta));
    var basicWeaponSelector =
        new ItemSelector.WeightedSelector(
            (d) -> LerpUtils.lerp(List.of(1000.0), 100.0), (d) -> 1.0);
    var generator =
        switch (slot) {
          case HEAD -> ItemGenerator.fromTag(slot, armorSelector, ItemTags.HEAD_ARMOR_ENCHANTABLE);
          case CHEST ->
              ItemGenerator.fromTag(slot, armorSelector, ItemTags.CHEST_ARMOR_ENCHANTABLE);
          case LEGS -> ItemGenerator.fromTag(slot, armorSelector, ItemTags.LEG_ARMOR_ENCHANTABLE);
          case FEET -> ItemGenerator.fromTag(slot, armorSelector, ItemTags.FOOT_ARMOR_ENCHANTABLE);
          case MAINHAND -> {
            if (RandomUtils.nextBoolean(random, 0.01))
              yield ItemGenerator.fromTag(
                  slot,
                  new ItemSelector.RecursiveChanceBased((d) -> 0.3),
                  ItemTags.TRIDENT_ENCHANTABLE,
                  ItemTags.MACE_ENCHANTABLE);
            yield ItemGenerator.fromTag(
                slot,
                basicWeaponSelector,
                RandomUtils.nextBoolean(random, 0.7) ? ItemTags.SWORDS : ItemTags.AXES);
          }
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

    var registry = world.getRegistryManager().get(RegistryKeys.ENCHANTMENT);
    var enchantments =
        registry.getOrCreateEntryList(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT).stream();
    return EnchantmentHelper.enchant(random, input, level, enchantments);
  }
}
