package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.data.ItemGenerator;
import org.codeberg.zenxarch.zombies.data.ItemSelector;
import org.codeberg.zenxarch.zombies.datagen.ZEnchantmentProviders;
import org.codeberg.zenxarch.zombies.datagen.ZItemTags;
import org.codeberg.zenxarch.zombies.random.LerpUtils;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

public abstract class Equipment {
  private static final List<Double> ARMOR_SELECT_CHANCE = List.of(0.9, 0.3);

  public static Item getItemForSlot(EquipmentSlot slot, Random random, double difficulty) {
    var basicWeaponSelector =
        new ItemSelector.WeightedSelector(
            (d) -> LerpUtils.lerp(List.of(1000.0), 100.0), (d) -> 1.0);
    var extraSelector = new ItemSelector.RecursiveChanceBased((d) -> 0.3);

    if (slot.equals(EquipmentSlot.MAINHAND)) {
      var rare = RandomUtils.nextBoolean(random, 0.01);
      var selector = rare ? extraSelector : basicWeaponSelector;
      var tag = ZItemTags.RARE_WEAPONS;
      if (!rare) {
        tag = ZItemTags.COMMON_WEAPONS;
        if (RandomUtils.nextBoolean(random, 0.3)) tag = ZItemTags.UNCOMMON_WEAPONS;
      }
      return ItemGenerator.fromTag(slot, selector, tag).generate(random, difficulty);
    }

    var armorSelector =
        new ItemSelector.RecursiveChanceBased(
            (delta) -> LerpUtils.lerp(ARMOR_SELECT_CHANCE, delta));

    var selector =
        switch (slot) {
          case HEAD, CHEST, LEGS, FEET -> armorSelector;
          case OFFHAND -> extraSelector;
          default -> null;
        };

    if (selector == null) return null;

    return ItemGenerator.fromTag(slot, selector, ZItemTags.fromSlot(slot))
        .generate(random, difficulty);
  }

  public static Optional<Item> getEquipmentForSlot(
      Random random, double difficulty, EquipmentSlot slot) {
    var item =
        ExtendedDifficulty.shouldSpawnWithEquipment(slot, difficulty)
            ? Equipment.getItemForSlot(slot, random, difficulty)
            : null;
    return Optional.ofNullable(item);
  }

  public static ItemStack enchant(
      ServerWorldAccess world, Random random, double difficulty, ItemStack input) {
    if (!ExtendedDifficulty.shouldEnchantEquipment(difficulty)) return input;

    var registryManager = world.getRegistryManager();
    EnchantmentHelper.applyEnchantmentProvider(
        input,
        registryManager,
        ZEnchantmentProviders.ZOMBIE_SPAWN_EQUIPMENT,
        new LocalDifficulty(world.getDifficulty(), 0, 0, 0),
        random);
    return input;
  }
}
