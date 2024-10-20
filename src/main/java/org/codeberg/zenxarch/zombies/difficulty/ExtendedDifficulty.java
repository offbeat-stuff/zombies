package org.codeberg.zenxarch.zombies.difficulty;

import it.unimi.dsi.fastutil.doubles.DoubleDoublePair;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.data.ItemGenerator;
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
    var weapons =
        player.getInventory().main.stream()
            .filter(
                f ->
                    f.isIn(ItemTags.AXES)
                        || f.isIn(ItemTags.SWORDS)
                        || f.isIn(ItemTags.MACE_ENCHANTABLE)
                        || f.isIn(ItemTags.TRIDENT_ENCHANTABLE));
    var head =
        Stream.of(player.getEquippedStack(EquipmentSlot.HEAD))
            .filter(f -> f.isIn(ItemTags.HEAD_ARMOR_ENCHANTABLE));
    var chest =
        Stream.of(player.getEquippedStack(EquipmentSlot.CHEST))
            .filter(f -> f.isIn(ItemTags.CHEST_ARMOR_ENCHANTABLE));
    var legs =
        Stream.of(player.getEquippedStack(EquipmentSlot.LEGS))
            .filter(f -> f.isIn(ItemTags.LEG_ARMOR_ENCHANTABLE));
    var feet =
        Stream.of(player.getEquippedStack(EquipmentSlot.FEET))
            .filter(f -> f.isIn(ItemTags.FOOT_ARMOR_ENCHANTABLE));

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

  private static double getRandomVariable(
      List<Double> avgl, List<Double> spreadl, double progress) {
    var avg = LerpUtils.lerp(avgl, progress);
    var spread = LerpUtils.lerp(spreadl, progress);
    return RandomUtils.nextDoubleAround(random, avg, spread);
  }

  private static List<Double> enchantChance = List.of(0.0, 0.0, 0.025, 0.05);
  private static List<Double> enchantLevelAvg = List.of(0.0, 5.0, 12.5, 28.5);
  private static List<Double> enchantLevelSpread = List.of(0.0, 2.0, 7.5, 2.5);

  public static int getEnchantLevel(double difficulty) {
    return RandomUtils.nextBoolean(random, LerpUtils.lerp(enchantChance, difficulty))
        ? 0
        : (int) getRandomVariable(enchantLevelAvg, enchantLevelSpread, difficulty);
  }

  private static List<Double> equipmentChance = List.of(0.0, 0.5);

  public static boolean shouldSpawnWithEquipment(double difficulty) {
    var atLeastOnce = LerpUtils.lerp(equipmentChance, difficulty);
    var chance = 1.0 - Math.pow(1.0 - atLeastOnce, 1.0 / 6.0);
    return RandomUtils.nextBoolean(random, chance);
  }
}
