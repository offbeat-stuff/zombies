package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.zrx;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;

public class ExtendedDifficulty {
  private double inhibitedHours = 0.0;
  private double moonSize = 0.0;
  private double difficulty = 0.0;
  private double days = 0.0;

  public ExtendedDifficulty(ServerWorld world, BlockPos pos) {
    if (world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()),
                            ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double)world.getMoonSize();
      inhibitedHours =
          (double)world.getWorldChunk(pos).getInhabitedTime() / (60 * 60 * 20);
    }

    difficulty = (double)world.getDifficulty().getId();
    days = (double)world.getTimeOfDay() / 24000.0;
  }

  private double midFactor() {
    var dayFactor = days / 100.0;
    var inhibFactor = MathHelper.clamp(inhibitedHours / 50.0, 0.0, 1.0) / 4.0;
    return MathHelper.clamp(difficulty * 0.5 *
                                ((moonSize * 0.125) + inhibFactor + dayFactor),
                            0.0, 1.0);
  }

  private double endFactor() {
    var dayFactor = MathHelper.clamp((days - 50.0) / 200.0, 0.0, 1.0) / 2.0;
    var inhibFactor =
        MathHelper.clamp((inhibitedHours - 20.0) / 100.0, 0.0, 1.0) / 2.0;

    if ((moonSize / 4.0) > dayFactor) {
      dayFactor = moonSize / 4.0;
    }

    return MathHelper.clamp(difficulty * 0.5 * (dayFactor + inhibFactor), 0.0,
                            1.0);
  }

  public int getTriesForSpawning() {
    if (days < 5.0) {
      return zrx.nextDouble() < 0.2 ? 1 : 0;
    }

    if (days < 50.0) {
      var mid = midFactor();
      return (int)MathHelper.clampedLerp(1.0, 15.0, mid);
    }

    var fin = endFactor();

    return (int)MathHelper.clampedLerp(15.0, 40.0, fin);
  }

  private static double mapToCurve(double input) {
    return (Math.pow((input + 1), -2) - 1) * (-4.0 / 3.0);
  }

  private static <T extends Item>
      Item getItemForList(Pair<List<T>, List<T>> list, boolean lowerHalf) {
    var f = lowerHalf ? list.getLeft() : list.getRight();
    var r = zrx.nextDouble();
    for (int i = f.size() - 1; i >= 0; i--) {
      var x = mapToCurve((double)i / f.size());
      if (r < x) {
        return f.get(i);
      }
    }
    return f.get(0);
  }

  private static Item getItemForSlot(EquipmentSlot slot, boolean lowerHalf) {
    switch (slot) {
    case HEAD:
      return getItemForList(Equipment.HEAD, lowerHalf);
    case CHEST:
      return getItemForList(Equipment.CHEST, lowerHalf);
    case LEGS:
      return getItemForList(Equipment.LEGS, lowerHalf);
    case FEET:
      return getItemForList(Equipment.FEET, lowerHalf);
    case MAINHAND:
      return getItemForList(Equipment.SWORD, lowerHalf);
    default:
      return Items.AIR;
    }
  }

  public Optional<Item> getEquipmentForSlot(EquipmentSlot slot) {
    if (days < 5.0) {
      return Optional.empty();
    }

    if (days < 50.0) {
      if (slot.equals(EquipmentSlot.OFFHAND) ||
          zrx.nextDouble() > midFactor() * 0.1) {
        return Optional.empty();
      }

      return Optional.of(getItemForSlot(slot, true));
    }

    if (slot.equals(EquipmentSlot.OFFHAND) && zrx.nextDouble() < 0.05) {
      return Optional.of(Items.SHIELD);
    }

    var ef = endFactor();
    if (zrx.nextDouble() < 0.5 * ef) {
      Optional.of(getItemForSlot(slot, true));
    }

    if (zrx.nextDouble() < 0.05 * ef) {
      Optional.of(getItemForSlot(slot, false));
    }

    return Optional.empty();
  }

  public ItemStack enchant(ItemStack input) {
    if (days < 5.0) {
      return input;
    }

    if (days < 50.0) {
      var mf = midFactor();
      return EnchantmentHelper.enchant(
          zrx, input, (int)MathHelper.clamp(5, 20, mf * zrx.nextDouble()),
          false);
    }

    var ef = endFactor();
    return EnchantmentHelper.enchant(
        zrx, input, (int)MathHelper.clamp(20, 40, ef * zrx.nextDouble()), true);
  }
}
