package org.codeberg.zenxarch.zombies.helper;

import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public abstract class ItemAttributesImpl {
  private static EntityAttributeInstance getAttributeInstance(
      EntityType<? extends LivingEntity> type, RegistryEntry<EntityAttribute> attribute) {
    var base = DefaultAttributeRegistry.get(type);
    if (!base.has(attribute)) base = DefaultAttributeContainer.builder().add(attribute).build();

    var container = new AttributeContainer(base);
    return container.getCustomInstance(attribute);
  }

  private static List<AttributeModifiersComponent.Entry> getAttributes(
      Item item, RegistryEntry<EntityAttribute> attribute, EquipmentSlot slot) {
    var attributes =
        item.getComponents()
            .getOrDefault(
                DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    return attributes.modifiers().stream()
        .filter(f -> f.slot().matches(slot))
        .filter(f -> f.attribute().equals(attribute))
        .toList();
  }

  public static double getAttributeValue(
      EntityType<? extends LivingEntity> type,
      Item item,
      RegistryEntry<EntityAttribute> attribute,
      EquipmentSlot slot) {
    var instance = getAttributeInstance(type, attribute);
    var attributes = getAttributes(item, attribute, slot);
    for (var entry : attributes) {
      instance.removeModifier(entry.modifier().id());
      instance.addTemporaryModifier(entry.modifier());
    }
    return instance.getValue();
  }

  public static double getZombieAttackDamage(Item item) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.ATTACK_DAMAGE, EquipmentSlot.MAINHAND);
  }

  public static double getZombieAttackSpeed(Item item) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.ATTACK_SPEED, EquipmentSlot.MAINHAND);
  }

  public static double getZombieArmor(Item item, EquipmentSlot slot) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.ARMOR, slot);
  }

  public static double getZombieArmorToughness(Item item, EquipmentSlot slot) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.ARMOR_TOUGHNESS, slot);
  }

  public static double getZombieKnockbackResistance(Item item, EquipmentSlot slot) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.KNOCKBACK_RESISTANCE, slot);
  }
}
