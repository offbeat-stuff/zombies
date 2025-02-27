package org.codeberg.zenxarch.zombies.difficulty;

import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.data.ItemAttributeUtils;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;

public abstract class DifficultyCalculations {
  private static final int TICKS_PER_HOUR = 60 * 60 * 20;
  private static final int TICKS_PER_DAY = 20 * 60 * 20;

  private DifficultyCalculations() {
    throw new IllegalStateException("Utility class");
  }

  private static double normalize(double value) {
    return Math.clamp(value, 0.0, 1.0);
  }

  private static double[] getPlayerScore(ServerWorld world, BlockPos pos) {
    var players = 0;
    var result = new double[] {0.0, 0.0};
    for (ServerPlayerEntity player : ZombieApocalypse.players(world)) {
      if (player.squaredDistanceTo(pos.toCenterPos()) > 128.0 * 128.0) continue;
      players++;
      result[0] += getPlayerScore(player);
      result[1] += player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(EntityType.ZOMBIE));
    }
    if (players == 0) return result;
    result[0] = normalize(result[0] / players);
    result[1] = normalize(result[1] / (players * 2500));
    return result;
  }

  private static double baseDifficulty(ServerWorld world, BlockPos pos) {
    var dayFactor = mapDays(world.getDifficulty(), getDays(world));
    var moonSize = (double) world.getMoonSize();
    var timeFactor = normalize(dayFactor * (1.0 + moonSize));
    var playerScore = getPlayerScore(world, pos);
    return timeFactor * 0.1
        + timeFactor * playerScore[0] * 0.5
        + playerScore[0] * 0.3
        + playerScore[1] * 0.1;
  }

  public static double calculateDifficulty(ServerWorld world, BlockPos pos) {
    var inhabitedTimeFactor = mapHours(world.getDifficulty(), getHoursInhabited(world, pos));
    if (inhabitedTimeFactor < 0.0) return 0.0;

    return inhabitedTimeFactor * baseDifficulty(world, pos);
  }

  private static double normalize(double value, double start, double end) {
    return MathHelper.clampedMap(value, start, end, 0.0, 1.0);
  }

  private static double mapDays(Difficulty difficulty, double days) {
    return switch (difficulty) {
      case PEACEFUL -> 0.0;
      case EASY -> normalize(days, 5.0, 250.0);
      case NORMAL -> normalize(days, 2.0, 150.0);
      case HARD -> normalize(days, 0.0, 100.0);
    };
  }

  private static double mapHours(Difficulty difficulty, double hours) {
    return switch (difficulty) {
      case PEACEFUL -> 0.0;
      case EASY -> normalize(hours, 8.0, 1.5);
      case NORMAL -> normalize(hours, 20.0, 1.5);
      case HARD -> normalize(hours, 35.0, 1.5);
    };
  }

  private static double getDays(ServerWorld world) {
    return (double) world.getTimeOfDay() / TICKS_PER_DAY;
  }

  private static double getHoursInhabited(World world, BlockPos pos) {
    if (world.isChunkLoaded(
        ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      return (double) world.getWorldChunk(pos).getInhabitedTime() / TICKS_PER_HOUR;
    }

    return 0.0;
  }

  private static Stream<ItemStack> items(ServerPlayerEntity player) {
    return player.getInventory().main.stream();
  }

  private static double getPlayerScore(ServerPlayerEntity player) {
    var weaponSpeedScore =
        scoreWeapon(
            player,
            DifficultyCalculations::getDamagePerSecond,
            Items.WOODEN_SWORD,
            Items.NETHERITE_SWORD);
    var weaponDamageScore =
        scoreWeapon(
            player, DifficultyCalculations::getAttackDamage, Items.WOODEN_AXE, Items.NETHERITE_AXE);
    var foodScore = items(player).mapToDouble(DifficultyCalculations::scoreFood).sum();
    foodScore = normalize(foodScore);

    var armor =
        player.getAttributeValue(EntityAttributes.ARMOR)
            + player.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS)
            + player.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);

    armor = normalize(armor, 0.0, 32.0);
    return (armor + weaponSpeedScore + weaponDamageScore) * (0.75 / 3.0) + foodScore * 0.25;
  }

  private static double getAttackDamage(ItemStack stack) {
    return ItemAttributeUtils.getAttributeValue(
        EntityType.PLAYER, stack, EntityAttributes.ATTACK_DAMAGE);
  }

  private static double getAttackSpeed(ItemStack stack) {
    return ItemAttributeUtils.getAttributeValue(
        EntityType.PLAYER, stack, EntityAttributes.ATTACK_SPEED);
  }

  private static double getDamagePerSecond(ItemStack stack) {
    return getAttackDamage(stack) * getAttackSpeed(stack);
  }

  private static double scoreFood(ItemStack stack) {
    var score = 0.0;
    if (stack.getComponents().contains(DataComponentTypes.FOOD))
      score = (double) stack.get(DataComponentTypes.FOOD).nutrition() * stack.getCount();
    return normalize(score, 0.0, 400.0);
  }

  private static boolean isAWeapon(ItemStack stack) {
    if (stack.isEmpty()) return false;
    if (stack.getComponents().contains(DataComponentTypes.ATTRIBUTE_MODIFIERS)) return true;
    return stack.hasEnchantments();
  }

  private static double scoreWeapon(
      ServerPlayerEntity player, ToDoubleFunction<ItemStack> scorer, Item minItem, Item maxItem) {
    var baseMin = scorer.applyAsDouble(minItem.getDefaultStack());
    var baseMax = scorer.applyAsDouble(maxItem.getDefaultStack());
    var bestScore =
        items(player)
            .filter(DifficultyCalculations::isAWeapon)
            .mapToDouble(scorer)
            .max()
            .orElse(0.0);
    return normalize(bestScore, baseMin, baseMax);
  }
}
