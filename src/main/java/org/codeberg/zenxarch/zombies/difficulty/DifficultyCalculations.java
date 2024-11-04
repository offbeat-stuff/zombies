package org.codeberg.zenxarch.zombies.difficulty;

import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.data.ItemAttributeUtils;

public abstract class DifficultyCalculations {
  private static final int TICKS_PER_HOUR = 60 * 60 * 20;
  private static final int TICKS_PER_DAY = 20 * 60 * 20;

  public static double calculateDifficulty(ServerWorld world, BlockPos pos) {
    var days = getDays(world, pos);
    var time = getTimeFactor(world, pos);
    var playerPredicate =
        EntityPredicates.EXCEPT_SPECTATOR.and(EntityPredicates.VALID_LIVING_ENTITY).negate();
    var scoreSum = 0.0;
    var zombieKills = 0;
    var players = 0;
    for (var player : world.getPlayers()) {
      if (playerPredicate.test(player)) continue;
      if (player.squaredDistanceTo(pos.toCenterPos()) > 128.0 * 128.0) continue;
      players++;
      scoreSum += getPlayerScore(player);
      zombieKills +=
          player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(EntityType.ZOMBIE));
    }

    var playerScore = players == 0 ? 0.0 : scoreSum / players;
    playerScore = MathHelper.clamp(playerScore, 0.0, 1.0);
    var timeFactor =
        (mapTimeFactor(world.getDifficulty(), days) + mapTimeFactor(world.getDifficulty(), time))
            * 0.5;

    var killScore = players == 0 ? 0.0 : zombieKills / players;
    killScore = MathHelper.clamp(killScore / 2500, 0.0, 1.0);

    return (timeFactor * 0.1)
        + (timeFactor * playerScore * 0.2)
        + (playerScore * 0.5)
        + (killScore * 0.2);
  }

  private static double mapTimeFactor(Difficulty difficulty, double timeFactor) {
    return switch (difficulty) {
      case PEACEFUL -> 0.0;
      case EASY -> MathHelper.clampedMap(timeFactor, 5.0, 250.0, 0.0, 1.0);
      case NORMAL -> MathHelper.clampedMap(timeFactor, 2.0, 150.0, 0.0, 1.0);
      case HARD -> MathHelper.clampedMap(timeFactor, 0.0, 100.0, 0.0, 1.0);
    };
  }

  private static double getDays(ServerWorld world, BlockPos pos) {
    return (double) world.getTimeOfDay() / TICKS_PER_DAY;
  }

  private static double getTimeFactor(World world, BlockPos pos) {
    double inhibitedHours = 0.0;
    double moonSize = 0.0;

    if (world.isChunkLoaded(
        ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      moonSize = (double) world.getMoonSize();
      inhibitedHours = (double) world.getWorldChunk(pos).getInhabitedTime() / TICKS_PER_HOUR;
    }

    return inhibitedHours * 1.5 * (1.0 + moonSize);
  }

  private static DoubleStream streamInventory(
      ServerPlayerEntity player, ToDoubleFunction<ItemStack> func) {
    return player.getInventory().main.stream().mapToDouble(func);
  }

  private static double getPlayerScore(ServerPlayerEntity player) {

    var weaponSpeedScore =
        streamInventory(player, DifficultyCalculations::scoreWeaponSpeed).max().orElse(0.0);
    var weaponDamageScore =
        streamInventory(player, DifficultyCalculations::scoreWeaponDamage).max().orElse(0.0);
    var foodScore = streamInventory(player, DifficultyCalculations::scoreFood).sum();
    foodScore = MathHelper.clamp(foodScore, 0.0, 1.0);

    var armor =
        player.getAttributeValue(EntityAttributes.GENERIC_ARMOR)
            + player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS)
            + player.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE);

    armor = MathHelper.clamp(armor / (32.0), 0.0, 1.0);
    return (armor + weaponSpeedScore + weaponDamageScore) * (0.75 / 3.0) + foodScore * 0.25;
  }

  private static double getAttackDamage(ItemStack stack) {
    return ItemAttributeUtils.getAttributeValue(
        EntityType.PLAYER, stack, EntityAttributes.GENERIC_ATTACK_DAMAGE);
  }

  private static double getAttackSpeed(ItemStack stack) {
    return ItemAttributeUtils.getAttributeValue(
        EntityType.PLAYER, stack, EntityAttributes.GENERIC_ATTACK_SPEED);
  }

  private static double scoreWeaponSpeed(ItemStack stack) {
    var score = getAttackDamage(stack) * getAttackSpeed(stack);
    return MathHelper.clamp(score / (8.0 * 1.6), 0.0, 1.0);
  }

  private static double scoreWeaponDamage(ItemStack stack) {
    var score = getAttackDamage(stack);
    return MathHelper.clamp(score / 10.0, 0.0, 1.0);
  }

  private static double scoreFood(ItemStack stack) {
    var score = 0.0;
    if (stack.getComponents().contains(DataComponentTypes.FOOD))
      score = stack.get(DataComponentTypes.FOOD).nutrition() * stack.getCount();
    return MathHelper.clamp(score / 400.0, 0.0, 1.0);
  }
}
