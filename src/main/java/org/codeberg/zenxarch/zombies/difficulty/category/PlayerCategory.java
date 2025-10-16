package org.codeberg.zenxarch.zombies.difficulty.category;

import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.CachedValue;
import org.codeberg.zenxarch.zombies.difficulty.DifficultyCategory;
import org.codeberg.zenxarch.zombies.difficulty.ItemAttributeUtils;
import org.codeberg.zenxarch.zombies.difficulty.entry.CachedPlayerBasedDifficultyEntry;

public interface PlayerCategory {

  private static AttachmentType<CachedValue> createCache(String id) {
    return AttachmentRegistry.create(Zombies.id(id));
  }

  public static final AttachmentType<CachedValue> WEAPON_DAMAGE =
      createCache("player/weapon_damage");
  public static final AttachmentType<CachedValue> DAMAGE_PER_SECOND =
      createCache("player/damage_per_second");
  public static final AttachmentType<CachedValue> FOOD = createCache("player/food");
  public static final AttachmentType<CachedValue> ARMOR = createCache("player/armor");

  public static final CachedPlayerBasedDifficultyEntry WEAPON_DAMAGE_ENTRY =
      new CachedPlayerBasedDifficultyEntry(WEAPON_DAMAGE, PlayerCategory::weaponDamageScore, 1);

  public static final CachedPlayerBasedDifficultyEntry DAMAGE_PER_SECOND_ENTRY =
      new CachedPlayerBasedDifficultyEntry(
          DAMAGE_PER_SECOND, PlayerCategory::weaponDamagePerSecondScore, 1);

  public static final CachedPlayerBasedDifficultyEntry FOOD_ENTRY =
      new CachedPlayerBasedDifficultyEntry(FOOD, PlayerCategory::foodScore, 1);

  public static final CachedPlayerBasedDifficultyEntry ARMOR_ENTRY =
      new CachedPlayerBasedDifficultyEntry(ARMOR, PlayerCategory::armorScore, 1);

  public static final Identifier PLAYER_CATEGORY = Zombies.id("player");

  public static void initialize() {
    DifficultyCategory.addDifficultyEntry(PLAYER_CATEGORY, WEAPON_DAMAGE_ENTRY);
    DifficultyCategory.addDifficultyEntry(PLAYER_CATEGORY, DAMAGE_PER_SECOND_ENTRY);
    DifficultyCategory.addDifficultyEntry(PLAYER_CATEGORY, FOOD_ENTRY);
    DifficultyCategory.addDifficultyEntry(PLAYER_CATEGORY, ARMOR_ENTRY);
  }

  private static double normalize(double value, double start, double end) {
    return MathHelper.clampedMap(value, start, end, 0.0, 1.0);
  }

  private static double weaponDamageScore(ServerWorld world, ServerPlayerEntity player) {
    return scoreWeapon(
        player, PlayerCategory::getAttackDamage, Items.WOODEN_AXE, Items.NETHERITE_AXE);
  }

  private static double weaponDamagePerSecondScore(ServerWorld world, ServerPlayerEntity player) {
    return scoreWeapon(
        player, PlayerCategory::getDamagePerSecond, Items.WOODEN_SWORD, Items.NETHERITE_SWORD);
  }

  private static double foodScore(ServerWorld world, ServerPlayerEntity player) {
    return items(player).mapToDouble(PlayerCategory::scoreFood).sum();
  }

  private static double armorScore(ServerWorld world, ServerPlayerEntity player) {
    return normalize(
        player.getAttributeValue(EntityAttributes.ARMOR)
            + player.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS)
            + player.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE),
        0.0,
        32.0);
  }

  private static Stream<ItemStack> items(ServerPlayerEntity player) {
    return player.getInventory().getMainStacks().stream();
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
        items(player).filter(PlayerCategory::isAWeapon).mapToDouble(scorer).max().orElse(0.0);
    return normalize(bestScore, baseMin, baseMax);
  }
}
