package org.codeberg.zenxarch.zombies.difficulty;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class NewZombie {
  public static ZombieEntity make(ServerWorld world, BlockPos pos) {
    var zombie = EntityType.ZOMBIE.create(world);
    zombie.setPosition(pos.getX(), pos.getY(), pos.getZ());
    return zombie;
  }

  private static void initMob(ZombieEntity zombie) {
    zombie.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)
        .addPersistentModifier(new EntityAttributeModifier(
            "Random spawn bonus",
            zombie.getRandom().nextTriangular(0.0, 0.11485000000000001),
            EntityAttributeModifier.Operation.MULTIPLY_BASE));
    zombie.setLeftHanded(zombie.getRandom().nextFloat() < 0.05);
  }

  private static List<Item> getEquipmentListForSlot(EquipmentSlot slot) {
    switch (slot) {
    case HEAD:
      return List.of(Items.LEATHER_HELMET, Items.IRON_HELMET,
                     Items.DIAMOND_HELMET, Items.NETHERITE_HELMET);
    case CHEST:
      return List.of(Items.LEATHER_CHESTPLATE, Items.IRON_CHESTPLATE,
                     Items.DIAMOND_CHESTPLATE, Items.NETHERITE_CHESTPLATE);
    case LEGS:
      return List.of(Items.LEATHER_LEGGINGS, Items.IRON_LEGGINGS,
                     Items.DIAMOND_LEGGINGS, Items.NETHERITE_LEGGINGS);
    case FEET:
      return List.of(Items.LEATHER_BOOTS, Items.IRON_BOOTS, Items.DIAMOND_BOOTS,
                     Items.NETHERITE_BOOTS);
    case MAINHAND:
      return List.of(Items.IRON_SHOVEL, Items.IRON_SWORD, Items.DIAMOND_SWORD,
                     Items.NETHERITE_AXE);
    default:
      return List.of(Items.AIR, Items.AIR, Items.AIR, Items.AIR);
    }
  }

  private static Item getEquipmentForSlot(EquipmentSlot slot, double level) {
    var list = getEquipmentListForSlot(slot);
    if (list == null) {
      return null;
    }

    if (level < 0.1) {
      return list.get(0);
    }

    if (level < 0.9) {
      return list.get(1);
    }

    if (level < 0.975) {
      return list.get(2);
    }

    return list.get(3);
  }

  private static void initEquipment(ZombieEntity zombie, double difficulty) {
    var random = zombie.getRandom();

    for (var slot : EquipmentSlot.values()) {
      if (!zombie.getEquippedStack(slot).isEmpty()) {
        continue;
      }

      if (random.nextDouble() > difficulty) {
        return;
      }

      zombie.equipStack(
          slot, getEquipmentForSlot(slot, difficulty * random.nextDouble())
                    .getDefaultStack());
    }
  }

  private static void updateEnchantments(ZombieEntity zombie,
                                         double difficulty) {
    var random = zombie.getRandom();
    if (!zombie.getMainHandStack().isEmpty() &&
        random.nextDouble() < difficulty) {
      zombie.equipStack(EquipmentSlot.MAINHAND,
                        EnchantmentHelper.enchant(
                            random, zombie.getMainHandStack(),
                            (int)MathHelper.clampedLerp(
                                1.0, 40.0, difficulty + random.nextDouble()),
                            true));
    }

    for (var slot : EquipmentSlot.values()) {
      if (zombie.getEquippedStack(slot).isEmpty()) {
        continue;
      }

      zombie.equipStack(slot,
                        EnchantmentHelper.enchant(
                            random, zombie.getEquippedStack(slot),
                            (int)MathHelper.clampedLerp(
                                1.0, 40.0, difficulty + random.nextDouble()),
                            true));
    }
  }

  private static void handleHalloween(ZombieEntity zombie) {
    if (zombie.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
      LocalDate localDate = LocalDate.now();
      int i = localDate.get(ChronoField.DAY_OF_MONTH);
      int j = localDate.get(ChronoField.MONTH_OF_YEAR);
      if (j == 10 && i == 31 && zombie.getRandom().nextFloat() < 0.25F) {
        zombie.equipStack(EquipmentSlot.HEAD,
                          new ItemStack(zombie.getRandom().nextFloat() < 0.1F
                                            ? Blocks.JACK_O_LANTERN
                                            : Blocks.CARVED_PUMPKIN));
        zombie.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
      }
    }
  }

  private static void initAttributes(ZombieEntity zombie, double difficulty) {
    var random = zombie.getRandom();
    zombie.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
        .setBaseValue(random.nextDouble() * 0.1F);

    zombie.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)
        .addPersistentModifier(new EntityAttributeModifier(
            "Random spawn bonus", random.nextDouble() * 0.05F,
            EntityAttributeModifier.Operation.ADDITION));
    double d = random.nextDouble() * 1.5 * (double)difficulty;
    if (d > 1.0) {
      zombie.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE)
          .addPersistentModifier(new EntityAttributeModifier(
              "Random zombie-spawn bonus", d,
              EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    if (random.nextFloat() < difficulty * 0.05F) {
      zombie.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS)
          .addPersistentModifier(new EntityAttributeModifier(
              "Leader zombie bonus", random.nextDouble() * 0.25 + 0.5,
              EntityAttributeModifier.Operation.ADDITION));
      zombie.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
          .addPersistentModifier(new EntityAttributeModifier(
              "Leader zombie bonus", random.nextDouble() * 3.0 + 1.0,
              EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
      zombie.setCanBreakDoors(!(zombie.getType().equals(EntityType.DROWNED)));
    }
  }

  public static void initialize(ServerWorld world, ZombieEntity zombie) {
    var difficulty =
        ExtendedDifficulty.calculateDifficulty(world, zombie.getBlockPos());

    initMob(zombie);

    zombie.setCanBreakDoors(zombie.getType().equals(EntityType.DROWNED) &&
                            zombie.getRandom().nextDouble() < difficulty * 0.1);
    initEquipment(zombie, difficulty);
    updateEnchantments(zombie, difficulty);
    handleHalloween(zombie);
    initAttributes(zombie, difficulty);
  }
}
