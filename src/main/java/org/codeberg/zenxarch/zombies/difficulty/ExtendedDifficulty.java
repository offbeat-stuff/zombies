package org.codeberg.zenxarch.zombies.difficulty;

import java.util.List;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ExtendedDifficulty {
  private static final Random random = Random.create();

  private static double getTimeFactor(World world, BlockPos pos) {
    double inhibitedHours = 0.0;
    double moonSize = 0.0;
    double days = 0.0;
    if (world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()),
                            ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double)world.getMoonSize();
      inhibitedHours =
          (double)world.getWorldChunk(pos).getInhabitedTime() / (60 * 60 * 20);
    }

    days = (double)world.getTimeOfDay() / 24000.0;

    return (days * 0.5) + (inhibitedHours * 1.5 * (1.0 + moonSize));
  }

  public static IExtendedDifficulty difficulty(World world, BlockPos pos) {
    var timeFactor = getTimeFactor(world, pos);

    switch (world.getDifficulty()) {
    case NORMAL:
      return getExtendedDifficulty(timeFactor, 10.0, 100.0, 500.0);
    case HARD:
      return getExtendedDifficulty(timeFactor, 5.0, 50.0, 250.0);
    default:
      return getExtendedDifficulty(timeFactor, 25.0, 200.0, 1000.0);
    }
  }

  private static IExtendedDifficulty getExtendedDifficulty(double timeFactor,
                                                           double grace,
                                                           double hard,
                                                           double nightmare) {
    if (timeFactor < grace) {
      return new GraceDifficulty(getProgress(timeFactor, 0.0, grace));
    }
    if (timeFactor < hard) {
      return new HardDifficulty(getProgress(timeFactor, grace, hard));
    }

    return new NightmareDifficulty(getProgress(timeFactor, hard, nightmare));
  }

  private static double getProgress(double time, double start, double end) {
    return MathHelper.clamp(MathHelper.getLerpProgress(time, start, end), 0.0,
                            1.0);
  }

  private static double mapToCurve(double input) {
    return (Math.pow((input + 1), -2) - 1) * (-4.0 / 3.0);
  }

  private static <T extends Item>
      Item getItemForList(Pair<List<T>, List<T>> list, boolean lowerHalf) {
    var f = lowerHalf ? list.getLeft() : list.getRight();
    var r = random.nextDouble();
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

  public static abstract class IExtendedDifficulty {
    protected double progress;

    public IExtendedDifficulty(double progress) { this.progress = progress; }

    public abstract int getTriesForSpawning();
    public abstract Optional<Item> getEquipmentForSlot(EquipmentSlot slot);
    public abstract ItemStack enchant(ItemStack input);
  }

  public static class GraceDifficulty extends IExtendedDifficulty {

    public GraceDifficulty(double progress) { super(progress); }

    @Override
    public int getTriesForSpawning() {
      return random.nextDouble() < 0.2 ? 1 : 0;
    }

    @Override
    public Optional<Item> getEquipmentForSlot(EquipmentSlot slot) {
      return Optional.empty();
    }

    @Override
    public ItemStack enchant(ItemStack input) {
      return input;
    }
  }

  public static class HardDifficulty extends IExtendedDifficulty {

    public HardDifficulty(double progress) { super(progress); }

    @Override
    public int getTriesForSpawning() {
      return (int)MathHelper.lerp(this.progress, 1.0, 5.1);
    }

    @Override
    public Optional<Item> getEquipmentForSlot(EquipmentSlot slot) {
      if (slot.equals(EquipmentSlot.OFFHAND) ||
          random.nextDouble() > this.progress * 0.1) {
        return Optional.empty();
      }

      return Optional.of(getItemForSlot(slot, true));
    }

    @Override
    public ItemStack enchant(ItemStack input) {
      return EnchantmentHelper.enchant(
          random, input,
          (int)MathHelper.clamp(5, 20, this.progress * random.nextDouble()),
          false);
    }
  }

  public static class NightmareDifficulty extends IExtendedDifficulty {

    public NightmareDifficulty(double progress) { super(progress); }

    @Override
    public int getTriesForSpawning() {
      return (int)MathHelper.lerp(this.progress, 5.1, 15.1);
    }

    @Override
    public Optional<Item> getEquipmentForSlot(EquipmentSlot slot) {
      if (slot.equals(EquipmentSlot.OFFHAND) && random.nextDouble() < 0.05) {
        return Optional.of(Items.SHIELD);
      }

      if (random.nextDouble() < ((0.4 * this.progress) + 0.1)) {
        Optional.of(getItemForSlot(slot, true));
      }

      if (random.nextDouble() < 0.05 * this.progress) {
        Optional.of(getItemForSlot(slot, false));
      }

      return Optional.empty();
    }

    @Override
    public ItemStack enchant(ItemStack input) {
      return EnchantmentHelper.enchant(
          random, input,
          (int)MathHelper.clamp(20, 40, this.progress * random.nextDouble()),
          true);
    }
  }
}
