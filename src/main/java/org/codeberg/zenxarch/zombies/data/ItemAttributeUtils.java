package org.codeberg.zenxarch.zombies.data;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

public abstract class ItemAttributeUtils {
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

  public static double getAttributeValue(
      EntityType<? extends LivingEntity> type,
      ItemStack item,
      RegistryEntry<EntityAttribute> attribute,
      EquipmentSlot slot) {
    var instance = getAttributeInstance(type, attribute);
    item.applyAttributeModifiers(
        slot,
        (attributeEntry, modifier) -> {
          if (attributeEntry.equals(attribute)) {
            instance.removeModifier(modifier);
            instance.addTemporaryModifier(modifier);
          }
        });
    return instance.getValue();
  }

  public static double getAttributeValue(
      EntityType<? extends LivingEntity> type,
      Item item,
      RegistryEntry<EntityAttribute> attribute) {
    return getAttributeValue(type, item, attribute, EquipmentSlot.MAINHAND);
  }

  public static double getAttributeValue(
      EntityType<? extends LivingEntity> type,
      ItemStack item,
      RegistryEntry<EntityAttribute> attribute) {
    return getAttributeValue(type, item, attribute, EquipmentSlot.MAINHAND);
  }

  public static double getZombieAttribute(Item item, RegistryEntry<EntityAttribute> attribute) {
    return getAttributeValue(EntityType.ZOMBIE, item, attribute);
  }

  public static double getZombieAttribute(
      Item item, RegistryEntry<EntityAttribute> attribute, EquipmentSlot slot) {
    return getAttributeValue(EntityType.ZOMBIE, item, attribute, slot);
  }
}
