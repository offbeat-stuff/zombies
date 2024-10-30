package org.codeberg.zenxarch.zombies.datagen;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.codeberg.zenxarch.zombies.Zombies;

public final class ZItemTags extends FabricTagProvider.ItemTagProvider {

  private static TagKey<Item> id(String path) {
    return TagKey.of(RegistryKeys.ITEM, Zombies.id(path));
  }

  public static TagKey<Item> WEAPONS = id("weapons");
  public static TagKey<Item> COMMON_WEAPONS = id("common_weapons");
  public static TagKey<Item> UNCOMMON_WEAPONS = id("uncommon_weapons");
  public static TagKey<Item> RARE_WEAPONS = id("rare_weapons");
  public static TagKey<Item> EXTRA_ITEMS = id("extra_items");
  public static TagKey<Item> HEAD_ARMOR = id("head_armor");
  public static TagKey<Item> CHEST_ARMOR = id("chest_armor");
  public static TagKey<Item> LEG_ARMOR = id("leg_armor");
  public static TagKey<Item> FEET_ARMOR = id("feet_armor");

  public ZItemTags(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
    super(output, completableFuture);
  }

  @Override
  protected void configure(WrapperLookup wrapperLookup) {
    getOrCreateTagBuilder(WEAPONS)
        .addTag(COMMON_WEAPONS)
        .addTag(UNCOMMON_WEAPONS)
        .addTag(RARE_WEAPONS);
    getOrCreateTagBuilder(COMMON_WEAPONS).addOptionalTag(ItemTags.SWORDS);
    getOrCreateTagBuilder(UNCOMMON_WEAPONS).addOptionalTag(ItemTags.AXES);
    getOrCreateTagBuilder(RARE_WEAPONS)
        .addOptionalTag(ItemTags.MACE_ENCHANTABLE)
        .addOptionalTag(ItemTags.TRIDENT_ENCHANTABLE);
    getOrCreateTagBuilder(EXTRA_ITEMS).add(Items.SHIELD);
    getOrCreateTagBuilder(HEAD_ARMOR).addOptionalTag(ItemTags.HEAD_ARMOR_ENCHANTABLE);
    getOrCreateTagBuilder(CHEST_ARMOR).addOptionalTag(ItemTags.CHEST_ARMOR_ENCHANTABLE);
    getOrCreateTagBuilder(LEG_ARMOR).addOptionalTag(ItemTags.LEG_ARMOR_ENCHANTABLE);
    getOrCreateTagBuilder(FEET_ARMOR).addOptionalTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);
  }

  public static TagKey<Item> fromSlot(EquipmentSlot slot) {
    return switch (slot) {
      case HEAD -> HEAD_ARMOR;
      case CHEST -> CHEST_ARMOR;
      case FEET -> FEET_ARMOR;
      case LEGS -> LEG_ARMOR;
      case OFFHAND -> EXTRA_ITEMS;
      default -> WEAPONS;
    };
  }
}
