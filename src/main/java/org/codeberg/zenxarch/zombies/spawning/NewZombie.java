package org.codeberg.zenxarch.zombies.spawning;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

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
    var difficulty = new ExtendedDifficulty(world, zombie.getBlockPos());
    var localDifficulty = world.getLocalDifficulty(zombie.getBlockPos())
                              .getClampedLocalDifficulty();

    initMob(zombie);

    zombie.setCanBreakDoors(zombie.getType().equals(EntityType.DROWNED) &&
                            zombie.getRandom().nextDouble() <
                                localDifficulty * 0.1);

    for (var slot : EquipmentSlot.values()) {
      if (!zombie.getEquippedStack(slot).isEmpty()) {
        continue;
      }
      var item = difficulty.getEquipmentForSlot(slot);
      if (item.isEmpty()) {
        continue;
      }
      zombie.equipStack(slot, item.get().getDefaultStack());
    }

    for (var slot : EquipmentSlot.values()) {
      if (zombie.getEquippedStack(slot).isEmpty()) {
        return;
      }

      zombie.equipStack(slot,
                        difficulty.enchant(zombie.getEquippedStack(slot)));
    }

    handleHalloween(zombie);
    initAttributes(zombie, localDifficulty);
  }
}
