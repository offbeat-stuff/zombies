package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.data.ItemAttributeUtils;
import org.codeberg.zenxarch.zombies.datagen.ZItemTags;

public abstract class DifficultyCalculations {
  private static final int TICKS_PER_HOUR = 60 * 60 * 20;
  private static final int TICKS_PER_DAY = 20 * 60 * 20;

  public static double calculateDifficulty(ServerWorld world, BlockPos pos) {
    var days = getDays(world, pos);
    var time = getTimeFactor(world, pos);
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

    var playerScore = players == 0 ? 0.0 : scoreSum / players;
    var timeFactor =
        (mapTimeFactor(world.getDifficulty(), days) + mapTimeFactor(world.getDifficulty(), time))
            * 0.5;

    return (timeFactor * playerScore * 0.25) + (playerScore * 0.75);
  }

  private static double mapTimeFactor(Difficulty difficulty, double timeFactor) {
    return switch (difficulty) {
      case PEACEFUL -> 0.0;
      case EASY -> MathHelper.clampedMap(timeFactor, 5.0, 500.0, 0.0, 1.0);
      case NORMAL -> MathHelper.clampedMap(timeFactor, 2.0, 250.0, 0.0, 1.0);
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

  private static double getPlayerScore(PlayerEntity player) {
    var totalScore =
        player.getInventory().main.stream()
            .filter(f -> f.isIn(ZItemTags.WEAPONS))
            .map(ItemStack::getItem)
            .mapToDouble(DifficultyCalculations::score)
            .max()
            .orElse(0.0);

    for (var slot : EquipmentSlot.values()) {
      if (!slot.getType().equals(EquipmentSlot.Type.HUMANOID_ARMOR)) continue;
      var stack = player.getEquippedStack(slot);
      if (!stack.isIn(ZItemTags.fromSlot(slot))) continue;
      totalScore += score(stack.getItem());
    }
    return totalScore;
  }

  private static double scoreWeapon(Item item) {
    var attackDamage =
        ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, item, EntityAttributes.GENERIC_ATTACK_DAMAGE);

    var attackSpeed =
        ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, item, EntityAttributes.GENERIC_ATTACK_SPEED);

    return attackDamage * attackSpeed;
  }

  private static double scoreArmor(ArmorItem armor) {
    var slot = armor.getSlotType();
    return ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, armor, EntityAttributes.GENERIC_ARMOR, slot)
        + ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, armor, EntityAttributes.GENERIC_ARMOR_TOUGHNESS, slot)
        + ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, armor, EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, slot);
  }

  private static double score(Item item) {
    return switch (item) {
      case ArmorItem armor -> scoreArmor(armor);
      default -> scoreWeapon(item);
    };
  }
}
