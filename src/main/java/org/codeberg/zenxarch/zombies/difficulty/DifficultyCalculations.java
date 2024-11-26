package org.codeberg.zenxarch.zombies.difficulty;

import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
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

  public static double calculateDifficulty(ServerWorld world, BlockPos pos) {
    var dayFactor = mapDays(world.getDifficulty(), getDays(world, pos));
    var inhabitedTimeFactor = mapHours(world.getDifficulty(), getHoursInhabited(world, pos));
    var moonSize = (double) world.getMoonSize();
    var scoreSum = 0.0;
    var zombieKills = 0;
    var players = 0;
    for (var player : ZombieApocalypse.players(world)) {
      if (player.squaredDistanceTo(pos.toCenterPos()) > 128.0 * 128.0) continue;
      players++;
      scoreSum += getPlayerScore(player);
      zombieKills +=
          player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(EntityType.ZOMBIE));
    }

    var playerScore = players == 0 ? 0.0 : scoreSum / players;
    playerScore = MathHelper.clamp(playerScore, 0.0, 1.0);
    var timeFactor = (dayFactor * inhabitedTimeFactor) * 0.5 * (1.0 + moonSize);

    var killScore = players == 0 ? 0.0 : zombieKills / players;
    killScore = MathHelper.clamp(killScore / 2500, 0.0, 1.0);

    return (timeFactor * 0.1)
        + (timeFactor * playerScore * 0.2)
        + (playerScore * 0.5)
        + (killScore * 0.2);
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

  private static double getDays(ServerWorld world, BlockPos pos) {
    return (double) world.getTimeOfDay() / TICKS_PER_DAY;
  }

  private static double getHoursInhabited(World world, BlockPos pos) {
    if (world.isChunkLoaded(
        ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
      return (double) world.getWorldChunk(pos).getInhabitedTime() / TICKS_PER_HOUR;
    }

    return 0.0;
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

  private static double scoreWeaponSpeed(ItemStack stack) {
    return normalize(
        getDamagePerSecond(stack),
        getDamagePerSecond(Items.WOODEN_SWORD.getDefaultStack()),
        getDamagePerSecond(Items.NETHERITE_SWORD.getDefaultStack()));
  }

  private static double scoreWeaponDamage(ItemStack stack) {
    return normalize(
        getAttackDamage(stack),
        getAttackDamage(Items.WOODEN_AXE.getDefaultStack()),
        getAttackDamage(Items.NETHERITE_AXE.getDefaultStack()));
  }

  private static double scoreFood(ItemStack stack) {
    var score = 0.0;
    if (stack.getComponents().contains(DataComponentTypes.FOOD))
      score = stack.get(DataComponentTypes.FOOD).nutrition() * stack.getCount();
    return normalize(score, 0.0, 400.0);
  }
}
