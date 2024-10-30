package org.codeberg.zenxarch.zombies.difficulty;

import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import org.codeberg.zenxarch.zombies.data.ItemGenerator;
import org.codeberg.zenxarch.zombies.datagen.ZEnchantmentProviders;
import org.codeberg.zenxarch.zombies.datagen.ZItemTags;
import org.codeberg.zenxarch.zombies.random.LerpUtils;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

public abstract class Equipment {
  private static Item selectItemFromTag(
      Random random, double difficulty, TagKey<Item> tag, EquipmentSlot slot) {
    var list = ItemGenerator.getList(tag, slot);
    return LerpUtils.recursiveSelect(random, difficulty, 0.3, list);
  }

  private static Item getWeapon(Random random, double difficulty) {
    if (RandomUtils.nextBoolean(random, 0.01))
      return selectItemFromTag(random, 1.0, ZItemTags.RARE_WEAPONS, EquipmentSlot.MAINHAND);
    var tag =
        RandomUtils.nextBoolean(random, 0.7)
            ? ZItemTags.COMMON_WEAPONS
            : ZItemTags.UNCOMMON_WEAPONS;
    return selectItemFromTag(random, difficulty, tag, EquipmentSlot.MAINHAND);
  }

  public static Item getItemForSlot(EquipmentSlot slot, Random random, double difficulty) {
    return switch (slot) {
      case MAINHAND -> getWeapon(random, difficulty);
      case HEAD, CHEST, LEGS, FEET, OFFHAND ->
          selectItemFromTag(random, difficulty, ZItemTags.fromSlot(slot), slot);
      default -> null;
    };
  }

  public static Optional<Item> getEquipmentForSlot(
      Random random, ExtendedDifficulty difficulty, EquipmentSlot slot) {
    var item =
        difficulty.shouldSpawnWithEquipment(slot)
            ? Equipment.getItemForSlot(slot, random, difficulty.getClampedLocalDifficulty())
            : null;
    return Optional.ofNullable(item);
  }

  public static ItemStack enchant(
      ServerWorldAccess world, Random random, ExtendedDifficulty difficulty, ItemStack input) {
    if (!difficulty.shouldEnchantEquipment()) return input;

    var registryManager = world.getRegistryManager();
    EnchantmentHelper.applyEnchantmentProvider(
        input, registryManager, ZEnchantmentProviders.ZOMBIE_SPAWN_EQUIPMENT, difficulty, random);
    return input;
  }
}
