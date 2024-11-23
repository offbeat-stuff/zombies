package org.codeberg.zenxarch.zombies.data

import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.AttributeModifiersComponent
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.attribute.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.entry.RegistryEntry

object ItemAttributeUtils {
    private fun getAttributeInstance(
        type: EntityType<out LivingEntity?>, attribute: RegistryEntry<EntityAttribute>
    ): EntityAttributeInstance? {
        var base = DefaultAttributeRegistry.get(type)
        if (!base.has(attribute)) base = DefaultAttributeContainer.builder().add(attribute).build()

        val container = AttributeContainer(base)
        return container.getCustomInstance(attribute)
    }

    private fun getAttributes(
        item: Item, attribute: RegistryEntry<EntityAttribute>, slot: EquipmentSlot
    ): List<AttributeModifiersComponent.Entry> {
        val attributes =
            item.components
                .getOrDefault(
                    DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent.DEFAULT
                )
        return attributes.modifiers().stream()
            .filter { f: AttributeModifiersComponent.Entry -> f.slot().matches(slot) }
            .filter { f: AttributeModifiersComponent.Entry -> f.attribute() == attribute }
            .toList()
    }

    fun getAttributeValue(
        type: EntityType<out LivingEntity?>,
        item: Item,
        attribute: RegistryEntry<EntityAttribute>,
        slot: EquipmentSlot
    ): Double {
        val instance = getAttributeInstance(type, attribute)
        val attributes = getAttributes(item, attribute, slot)
        for (entry in attributes) {
            instance!!.removeModifier(entry.modifier().id())
            instance.addTemporaryModifier(entry.modifier())
        }
        return instance!!.value
    }

    fun getAttributeValue(
        type: EntityType<out LivingEntity?>,
        item: ItemStack,
        attribute: RegistryEntry<EntityAttribute>,
        slot: EquipmentSlot?
    ): Double {
        val instance = getAttributeInstance(type, attribute)
        item.applyAttributeModifiers(
            slot
        ) { attributeEntry: RegistryEntry<EntityAttribute?>, modifier: EntityAttributeModifier? ->
            if (attributeEntry == attribute) {
                instance!!.removeModifier(modifier)
                instance.addTemporaryModifier(modifier)
            }
        }
        return instance!!.value
    }

    fun getAttributeValue(
        type: EntityType<out LivingEntity?>,
        item: Item,
        attribute: RegistryEntry<EntityAttribute>
    ): Double {
        return getAttributeValue(type, item, attribute, EquipmentSlot.MAINHAND)
    }

    fun getAttributeValue(
        type: EntityType<out LivingEntity?>,
        item: ItemStack,
        attribute: RegistryEntry<EntityAttribute>
    ): Double {
        return getAttributeValue(type, item, attribute, EquipmentSlot.MAINHAND)
    }

    fun getZombieAttribute(item: Item, attribute: RegistryEntry<EntityAttribute>): Double {
        return getAttributeValue(EntityType.ZOMBIE, item, attribute)
    }

    fun getZombieAttribute(
        item: Item, attribute: RegistryEntry<EntityAttribute>, slot: EquipmentSlot
    ): Double {
        return getAttributeValue(EntityType.ZOMBIE, item, attribute, slot)
    }
}
