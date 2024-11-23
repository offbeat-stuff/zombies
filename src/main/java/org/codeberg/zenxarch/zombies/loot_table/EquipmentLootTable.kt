package org.codeberg.zenxarch.zombies.loot_table

import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.loot.LootPool
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LeafEntry
import net.minecraft.loot.provider.number.ConstantLootNumberProvider

object EquipmentLootTable {
    private val helmets: List<Item> = java.util.List.of(
        Items.LEATHER_HELMET,
        Items.CHAINMAIL_HELMET,
        Items.GOLDEN_HELMET,
        Items.IRON_HELMET,
        Items.DIAMOND_HELMET,
        Items.NETHERITE_HELMET
    )
    private val chestplates: List<Item> = java.util.List.of(
        Items.LEATHER_CHESTPLATE,
        Items.CHAINMAIL_CHESTPLATE,
        Items.GOLDEN_CHESTPLATE,
        Items.IRON_CHESTPLATE,
        Items.DIAMOND_CHESTPLATE,
        Items.NETHERITE_CHESTPLATE
    )
    private val leggings: List<Item> = java.util.List.of(
        Items.LEATHER_LEGGINGS,
        Items.CHAINMAIL_LEGGINGS,
        Items.GOLDEN_LEGGINGS,
        Items.IRON_LEGGINGS,
        Items.DIAMOND_LEGGINGS,
        Items.NETHERITE_LEGGINGS
    )
    private val boots: List<Item> = java.util.List.of(
        Items.LEATHER_BOOTS,
        Items.CHAINMAIL_BOOTS,
        Items.GOLDEN_BOOTS,
        Items.IRON_BOOTS,
        Items.DIAMOND_BOOTS,
        Items.NETHERITE_BOOTS
    )
    private val swords: List<Item> = java.util.List.of(
        Items.WOODEN_SWORD,
        Items.STONE_SWORD,
        Items.GOLDEN_SWORD,
        Items.IRON_SWORD,
        Items.DIAMOND_SWORD,
        Items.NETHERITE_SWORD
    )
    private val axes: List<Item> = java.util.List.of(
        Items.WOODEN_AXE,
        Items.STONE_AXE,
        Items.GOLDEN_AXE,
        Items.IRON_AXE,
        Items.DIAMOND_AXE,
        Items.NETHERITE_AXE
    )

    private val weights = listOf(1000, 250, 50, 50, 5, 1, 100)
    private val qualities: List<Int> = java.util.List.of(-1000, -250, 200, 200, 5, 0, 50)

    private fun getWeight(item: Item): Int {
        if (helmets.contains(item)) return weights[helmets.indexOf(
            item
        )]
        if (chestplates.contains(item)) return weights[chestplates.indexOf(
            item
        )]
        if (leggings.contains(item)) return weights[leggings.indexOf(
            item
        )]
        if (boots.contains(item)) return weights[boots.indexOf(
            item
        )]
        if (swords.contains(item)) return weights[swords.indexOf(
            item
        )]
        if (axes.contains(item)) return weights[axes.indexOf(
            item
        )]
        return weights.last()
    }

    private fun getQuality(item: Item): Int {
        if (helmets.contains(item)) return qualities[helmets.indexOf(
            item
        )]
        if (chestplates.contains(item)) return qualities[chestplates.indexOf(
            item
        )]
        if (leggings.contains(item)) return qualities[leggings.indexOf(
            item
        )]
        if (boots.contains(item)) return qualities[boots.indexOf(
            item
        )]
        if (swords.contains(item)) return qualities[swords.indexOf(
            item
        )]
        if (axes.contains(item)) return qualities[axes.indexOf(
            item
        )]
        return qualities.last()
    }

    @JvmStatic
    val helmetPool: LootPool.Builder
        get() = push(
            getPoolFromList(helmets),
            Items.TURTLE_HELMET
        )

    @JvmStatic
    val chestplatePool: LootPool.Builder
        get() = getPoolFromList(chestplates)

    @JvmStatic
    val leggingsPool: LootPool.Builder
        get() = getPoolFromList(leggings)

    @JvmStatic
    val bootsPool: LootPool.Builder
        get() = getPoolFromList(boots)

    @JvmStatic
    val swordsPool: LootPool.Builder
        get() = getPoolFromList(swords)

    @JvmStatic
    val axesPool: LootPool.Builder
        get() = getPoolFromList(axes)

    private fun getPoolFromList(items: List<Item>): LootPool.Builder {
        var result = singleRollLootPool()
        for (item in items) result = push(result, item)
        return result
    }

    private fun push(builder: LootPool.Builder, item: Item): LootPool.Builder {
        return builder.with(equipmentEntry(item, getWeight(item), getQuality(item)))
    }

    private fun singleRollLootPool(): LootPool.Builder {
        return LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
    }

    private fun equipmentEntry(item: Item, weight: Int, quality: Int): LeafEntry.Builder<*> {
        return ItemEntry.builder(item).weight(weight).quality(quality)
    }
}
