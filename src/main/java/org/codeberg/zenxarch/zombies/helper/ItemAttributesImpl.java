package org.codeberg.zenxarch.zombies.helper;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.registry.entry.RegistryEntry;

public abstract class ItemAttributesImpl {
  public static double getAttributeValue(
      EntityType<? extends LivingEntity> type,
      Item item,
      RegistryEntry<EntityAttribute> attribute) {
    var baseAttributes = new AttributeContainer(DefaultAttributeRegistry.get(type));
    var attributes =
        item.getComponents()
            .getOrDefault(
                DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT);
    attributes.applyModifiers(
        EquipmentSlot.MAINHAND,
        (registryEntry, entityAttributeModifier) -> {
          EntityAttributeInstance entityAttributeInstance =
              baseAttributes.getCustomInstance(registryEntry);
          if (entityAttributeInstance != null) {
            entityAttributeInstance.removeModifier(entityAttributeModifier.id());
            entityAttributeInstance.addTemporaryModifier(entityAttributeModifier);
          }
        });
    return baseAttributes.getValue(attribute);
  }

  public static double getZombieAttackDamage(Item item) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.GENERIC_ATTACK_DAMAGE);
  }

  public static double getZombieAttackSpeed(Item item) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.GENERIC_ATTACK_SPEED);
  }

  public static double getZombieAttackKnockback(Item item) {
    return ItemAttributesImpl.getAttributeValue(
        EntityType.ZOMBIE, item, EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
  }
}
