package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.zrx;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
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

  public int getTriesForSpawning() {
    if (days < 5.0) {
      return zrx.nextDouble() < 0.2 ? 1 : 0;
    }

    if (days < 50.0) {
      var dayFactor = days / 100.0;
      var inhibFactor = MathHelper.clamp(inhibitedHours / 50.0, 0.0, 1.0) / 4.0;
      return (int)MathHelper.clampedLerp(
          1.0, 15.0,
          difficulty * 0.5 * ((moonSize * 0.125) + inhibFactor + dayFactor));
    }

    var dayFactor = MathHelper.clamp((days - 50.0) / 200.0, 0.0, 1.0) / 2.0;
    var inhibFactor =
        MathHelper.clamp((inhibitedHours - 20.0) / 100.0, 0.0, 1.0) / 2.0;

    if ((moonSize / 4.0) > dayFactor) {
      dayFactor = moonSize / 4.0;
    }

    return (int)MathHelper.clampedLerp(
        15.0, 40.0, difficulty * 0.5 * (dayFactor + inhibFactor));
  }

  private static <T extends Item>
      Item getItemForList(Pair<List<T>, List<T>> list, boolean lowerHalf) {
    if (lowerHalf) {
      var f = list.getLeft();
      var index = zrx.nextBetween(0, f.size() - 1);
      return f.get(index);
    }
    var f = list.getRight();
    var index = zrx.nextBetween(0, f.size() - 1);
    return f.get(index);
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
      if (slot.equals(EquipmentSlot.OFFHAND)) {
        return Optional.empty();
      }

      return Optional.of(getItemForSlot(slot, false));
    }

    if (slot.equals(EquipmentSlot.OFFHAND) && zrx.nextDouble() < 0.05) {
      return Optional.of(Items.SHIELD);
    }

    return Optional.of(getItemForSlot(slot, true));
  }
}
