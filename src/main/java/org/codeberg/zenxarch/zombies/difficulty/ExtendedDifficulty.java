package org.codeberg.zenxarch.zombies.difficulty;

import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.data.ItemGenerator;
import org.codeberg.zenxarch.zombies.datagen.ZItemTags;
import org.codeberg.zenxarch.zombies.random.LerpUtils;
import org.codeberg.zenxarch.zombies.random.RandomUtils;

public class ExtendedDifficulty {
  private static final int TICKS_PER_HOUR = 60 * 60 * 20;
  private static final int TICKS_PER_DAY = 20 * 60 * 20;

  private static DoubleDoublePair getTimeFactor(World world, BlockPos pos) {
    double inhibitedHours = 0.0;
    double moonSize = 0.0;
    double days = 0.0;

    if (world.isChunkLoaded(
        ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double) world.getMoonSize();
      inhibitedHours = (double) world.getWorldChunk(pos).getInhabitedTime() / TICKS_PER_HOUR;
    }

    days = (double) world.getTimeOfDay() / TICKS_PER_DAY;

    return DoubleDoublePair.of(days, inhibitedHours * 1.5 * (1.0 + moonSize));
  }

  private static double getPlayerScore(PlayerEntity player) {
    var weapons = player.getInventory().main.stream().filter(f -> f.isIn(ZItemTags.WEAPONS));
    var head =
        Stream.of(player.getEquippedStack(EquipmentSlot.HEAD))
            .filter(f -> f.isIn(ZItemTags.HEAD_ARMOR));
    var chest =
        Stream.of(player.getEquippedStack(EquipmentSlot.CHEST))
            .filter(f -> f.isIn(ZItemTags.CHEST_ARMOR));
    var legs =
        Stream.of(player.getEquippedStack(EquipmentSlot.LEGS))
            .filter(f -> f.isIn(ZItemTags.LEG_ARMOR));
    var feet =
        Stream.of(player.getEquippedStack(EquipmentSlot.FEET))
            .filter(f -> f.isIn(ZItemTags.FEET_ARMOR));

    return Stream.of(weapons, head, chest, legs, feet)
        .mapToDouble(
            v -> v.map(ItemStack::getItem).mapToDouble(ItemGenerator::score).max().orElse(0.0))
        .sum();
  }

  public static double getDifficulty(ServerWorld world, BlockPos pos) {
    var time = getTimeFactor(world, pos);
    var left = LerpUtils.clampedLerpProgress(time.leftDouble(), 0.0, 100.0);
    var right = LerpUtils.clampedLerpProgress(time.rightDouble(), 0.0, 100.0);
    var timeFactor = (left + right) / 2;
    var playerPredicate =
        EntityPredicates.EXCEPT_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY).negate();
    var scoreSum = 0.0;
    var players = 0;
    for (var player : world.getPlayers()) {
      if (playerPredicate.test(player)) continue;
      if (player.squaredDistanceTo(pos.toCenterPos()) > 128.0 * 128.0) continue;
      players++;
      scoreSum += getPlayerScore(player);
    }
    return timeFactor * 0.25 + LerpUtils.clampedLerpProgress(scoreSum / players, 8.0, 64.0) * 0.75;
  }

  private static final Random random = Random.create();

  public static int getMaxZombies(double difficulty) {
    return (int) MathHelper.clampedLerp(50.0, 250.0, difficulty);
  }

  private static List<Double> enchantChance = List.of(0.0, 0.0, 0.025, 0.05);

  public static boolean shouldEnchantEquipment(double difficulty) {
    return RandomUtils.nextBoolean(random, LerpUtils.lerp(enchantChance, difficulty));
  }

  public static boolean shouldSpawnWithEquipment(EquipmentSlot slot, double difficulty) {
    return switch (slot) {
      case MAINHAND, OFFHAND -> RandomUtils.nextBoolean(random, difficulty);
      default -> RandomUtils.nextBoolean(random, difficulty, 4);
    };
  }
}
