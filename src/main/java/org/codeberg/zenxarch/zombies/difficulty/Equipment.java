package org.codeberg.zenxarch.zombies.difficulty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import org.codeberg.zenxarch.zombies.Zombies;

public abstract class Equipment {

  private static final Comparator<ArmorItem> ArmorRanking = (a, b)
      -> a.getProtection() == b.getProtection()
             ? Float.compare(a.getToughness(), b.getToughness())
             : Float.compare(a.getProtection(), b.getProtection());

  private static float getAttackDamage(Item item) {
    return (float)item.getComponents()
        .getOrDefault(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                      AttributeModifiersComponent.DEFAULT)
        .applyOperations(
            ZombieEntity.createZombieAttributes().build().getBaseValue(
                EntityAttributes.GENERIC_ATTACK_DAMAGE),
            EquipmentSlot.MAINHAND);
  }

  private static final Comparator<SwordItem> SwordRanking =
      (a, b) -> Float.compare(getAttackDamage(a), getAttackDamage(b));
  private static final Comparator<AxeItem> AxeRanking =
      (a, b) -> Float.compare(getAttackDamage(a), getAttackDamage(b));

  public static Pair<List<ArmorItem>, List<ArmorItem>> HEAD =
      getSortedList(EquipmentType.HEAD, ArmorRanking);
  public static Pair<List<ArmorItem>, List<ArmorItem>> CHEST =
      getSortedList(EquipmentType.CHEST, ArmorRanking);
  public static Pair<List<ArmorItem>, List<ArmorItem>> LEGS =
      getSortedList(EquipmentType.LEGS, ArmorRanking);
  public static Pair<List<ArmorItem>, List<ArmorItem>> FEET =
      getSortedList(EquipmentType.FEET, ArmorRanking);
  public static Pair<List<SwordItem>, List<SwordItem>> SWORD =
      getSortedList(EquipmentType.SWORD, SwordRanking);
  public static Pair<List<AxeItem>, List<AxeItem>> AXE =
      getSortedList(EquipmentType.AXE, AxeRanking);

  @SuppressWarnings("unchecked")
  private static <T extends Item> Pair<List<T>, List<T>>
  getSortedList(EquipmentType type, Comparator<T> compare) {
    try {

      var list = findAllMatching(type)
                     .stream()
                     .map(f -> (T)f)
                     .sorted(compare)
                     .toList();

      var a = list.stream()
                  .filter(f -> compare.compare(f, (T)type.diamond) < 0)
                  .toList();
      var b = list.stream()
                  .filter(f -> compare.compare(f, (T)type.diamond) >= 0)
                  .toList();
      return new Pair<List<T>, List<T>>(a, b);
    } catch (ClassCastException e) {
      Zombies.LOGGER.error("Casting failed {}", e);
      return new Pair<List<T>, List<T>>(List.of(), List.of());
    }
  }

  private static List<Item> findAllMatching(EquipmentType type) {
    var arrayList = new ArrayList<Item>();
    for (var id : Registries.ITEM.getIds()) {
      if (!id.getPath().endsWith(type.name)) {
        continue;
      }
      var item = Registries.ITEM.get(id);
      if (type.predicate.test(item)) {
        arrayList.add(item);
      }
    }
    return List.copyOf(arrayList);
  }

  private static enum EquipmentType {
    HEAD(
        "helmet",
        item -> isArmorItem(item, ArmorItem.Type.HELMET), Items.DIAMOND_HELMET),
    CHEST("chestplate",
          item
          -> isArmorItem(item, ArmorItem.Type.CHESTPLATE),
          Items.DIAMOND_CHESTPLATE),
    LEGS("leggings",
         item
         -> isArmorItem(item, ArmorItem.Type.LEGGINGS),
         Items.DIAMOND_LEGGINGS),
    FEET("boots",
         item -> isArmorItem(item, ArmorItem.Type.BOOTS), Items.DIAMOND_BOOTS),
    SWORD("sword", item -> item instanceof SwordItem, Items.DIAMOND_SWORD),
    AXE("axe", item -> item instanceof AxeItem, Items.DIAMOND_AXE);

    public final String name;
    public final Predicate<Item> predicate;
    public final Item diamond;

    private EquipmentType(String name, Predicate<Item> predicate,
                          Item diamond) {
      this.name = name;
      this.predicate = predicate;
      this.diamond = diamond;
    }

    private static boolean isArmorItem(Item item, ArmorItem.Type type) {
      if (item instanceof ArmorItem armor) {
        return armor.getType().equals(type);
      }
      return false;
    }
  }
}
