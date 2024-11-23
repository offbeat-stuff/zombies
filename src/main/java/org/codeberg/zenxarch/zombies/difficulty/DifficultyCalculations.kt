package org.codeberg.zenxarch.zombies.difficulty

import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.EntityType
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.stat.Stats
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkSectionPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.Difficulty
import net.minecraft.world.World
import org.codeberg.zenxarch.zombies.data.ItemAttributeUtils
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse.Companion.players
import java.util.stream.DoubleStream

object DifficultyCalculations {
    private const val TICKS_PER_HOUR = 60 * 60 * 20
    private const val TICKS_PER_DAY = 20 * 60 * 20

    fun calculateDifficulty(world: ServerWorld, pos: BlockPos): Double {
        val days = getDays(world, pos)
        val time = getTimeFactor(world, pos)
        var scoreSum = 0.0
        var zombieKills = 0
        var players = 0
        for (player in players(world)) {
            if (player.squaredDistanceTo(pos.toCenterPos()) > 128.0 * 128.0) continue
            players++
            scoreSum += getPlayerScore(player)
            zombieKills +=
                player.statHandler.getStat(Stats.KILLED.getOrCreateStat(EntityType.ZOMBIE))
        }

        var playerScore = if (players == 0) 0.0 else scoreSum / players
        playerScore = MathHelper.clamp(playerScore, 0.0, 1.0)
        val timeFactor =
            ((mapTimeFactor(world.difficulty, days) + mapTimeFactor(world.difficulty, time))
                    * 0.5)

        var killScore = if (players == 0) 0.0 else (zombieKills / players).toDouble()
        killScore = MathHelper.clamp(killScore / 2500, 0.0, 1.0)

        return ((timeFactor * 0.1)
                + (timeFactor * playerScore * 0.2)
                + (playerScore * 0.5)
                + (killScore * 0.2))
    }

    private fun mapTimeFactor(difficulty: Difficulty, timeFactor: Double): Double {
        return when (difficulty) {
            Difficulty.PEACEFUL -> 0.0
            Difficulty.EASY -> MathHelper.clampedMap(timeFactor, 5.0, 250.0, 0.0, 1.0)
            Difficulty.NORMAL -> MathHelper.clampedMap(timeFactor, 2.0, 150.0, 0.0, 1.0)
            Difficulty.HARD -> MathHelper.clampedMap(timeFactor, 0.0, 100.0, 0.0, 1.0)
        }
    }

    private fun getDays(world: ServerWorld, pos: BlockPos): Double {
        return world.timeOfDay.toDouble() / TICKS_PER_DAY
    }

    private fun getTimeFactor(world: World, pos: BlockPos): Double {
        var inhibitedHours = 0.0
        var moonSize = 0.0

        if (world.isChunkLoaded(
                ChunkSectionPos.getSectionCoord(pos.x), ChunkSectionPos.getSectionCoord(pos.z)
            )
        ) {
            moonSize = world.moonSize.toDouble()
            inhibitedHours = world.getWorldChunk(pos).inhabitedTime.toDouble() / TICKS_PER_HOUR
        }

        return inhibitedHours * 1.5 * (1.0 + moonSize)
    }

    private fun streamInventory(
        player: ServerPlayerEntity, func: (ItemStack) -> Double
    ): DoubleStream {
        return player.inventory.main.stream().mapToDouble(func)
    }

    private fun getPlayerScore(player: ServerPlayerEntity): Double {
        val weaponSpeedScore =
            streamInventory(player, ::scoreWeaponSpeed).max().orElse(0.0)
        val weaponDamageScore =
            streamInventory(player, ::scoreWeaponDamage)
                .max().orElse(0.0)
        var foodScore = streamInventory(
            player, ::scoreFood
        ).sum()
        foodScore = MathHelper.clamp(foodScore, 0.0, 1.0)

        var armor =
            (player.getAttributeValue(EntityAttributes.ARMOR)
                    + player.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS)
                    + player.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE))

        armor = MathHelper.clamp(armor / (32.0), 0.0, 1.0)
        return (armor + weaponSpeedScore + weaponDamageScore) * (0.75 / 3.0) + foodScore * 0.25
    }

    private fun getAttackDamage(stack: ItemStack): Double {
        return ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, stack, EntityAttributes.ATTACK_DAMAGE
        )
    }

    private fun getAttackSpeed(stack: ItemStack): Double {
        return ItemAttributeUtils.getAttributeValue(
            EntityType.PLAYER, stack, EntityAttributes.ATTACK_SPEED
        )
    }

    private fun getDamagePerSecond(stack: ItemStack): Double {
        return getAttackDamage(stack) * getAttackSpeed(stack)
    }

    private fun scoreWeaponSpeed(stack: ItemStack): Double {
        return MathHelper.clampedMap(
            getDamagePerSecond(stack),
            getDamagePerSecond(ItemStack.EMPTY),
            getDamagePerSecond(Items.NETHERITE_SWORD.defaultStack),
            0.0,
            1.0
        )
    }

    private fun scoreWeaponDamage(stack: ItemStack): Double {
        return MathHelper.clampedMap(
            getAttackDamage(stack),
            getAttackDamage(ItemStack.EMPTY),
            getAttackDamage(Items.NETHERITE_AXE.defaultStack),
            0.0,
            1.0
        )
    }

    private fun scoreFood(stack: ItemStack): Double {
        var score = 0.0
        if (stack.components.contains(DataComponentTypes.FOOD)) score = (stack.get(
            DataComponentTypes.FOOD
        )!!
            .nutrition() * stack.count).toDouble()
        return MathHelper.clamp(score / 400.0, 0.0, 1.0)
    }
}
