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
import org.codeberg.zenxarch.zombies.math.RandomUtils;

public abstract class Equipment {
  private static Optional<Item> selectItemFromTag(
      Random random, double difficulty, TagKey<Item> tag, EquipmentSlot slot) {
    var list = ItemGenerator.getList(tag, slot);
    return RandomUtils.sample(
        list, () -> RandomUtils.logDist(random, 0.5 * difficulty, 0.75 / 6.0));
  }

  private static Optional<Item> getWeapon(Random random, double difficulty) {
    if (RandomUtils.nextBoolean(random, 0.001)) {
      var list = ItemGenerator.getList(ZItemTags.RARE_WEAPONS, EquipmentSlot.MAINHAND);
      return RandomUtils.sample(list, () -> random.nextDouble());
    }
    var tag =
        RandomUtils.nextBoolean(random, 0.75)
            ? ZItemTags.COMMON_WEAPONS
            : ZItemTags.UNCOMMON_WEAPONS;
    return selectItemFromTag(random, difficulty, tag, EquipmentSlot.MAINHAND);
  }

  public static Optional<Item> getItemForSlot(
      EquipmentSlot slot, Random random, double difficulty) {
    return switch (slot) {
      case MAINHAND -> getWeapon(random, difficulty);
      case HEAD, CHEST, LEGS, FEET, OFFHAND ->
          selectItemFromTag(random, difficulty, ZItemTags.fromSlot(slot), slot);
      default -> Optional.empty();
    };
  }

  public static Optional<Item> getEquipmentForSlot(
      Random random, ExtendedDifficulty difficulty, EquipmentSlot slot) {
    if (!difficulty.shouldSpawnWithEquipment(slot)) return Optional.empty();
    return Equipment.getItemForSlot(slot, random, difficulty.getClampedLocalDifficulty());
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
