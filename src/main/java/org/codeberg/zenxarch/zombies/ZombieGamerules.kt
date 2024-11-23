package org.codeberg.zenxarch.zombies

import net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry
import net.minecraft.text.MutableText
import net.minecraft.text.PlainTextContent
import net.minecraft.text.Style
import net.minecraft.util.Formatting
import net.minecraft.world.GameRules
import net.minecraft.world.GameRules.BooleanRule
import net.minecraft.world.GameRules.IntRule

object ZombieGamerules {
    private val ZOMBIES_GENERAL: CustomGameRuleCategory = CustomGameRuleCategory(
        Zombies.id("general"),
        MutableText.of(PlainTextContent.of("Zombies"))
            .setStyle(Style.EMPTY.withColor(Formatting.YELLOW))
    )

    private fun newGameRule(name: String, defaultValue: Int) =
        GameRuleRegistry.register(name, ZOMBIES_GENERAL, GameRuleFactory.createIntRule(defaultValue))
    private fun newGameRule(name: String, defaultValue: Boolean) =
        GameRuleRegistry.register(name, ZOMBIES_GENERAL, GameRuleFactory.createBooleanRule(defaultValue))

    @kotlin.jvm.JvmField
    val MAX_ZOMBIES: GameRules.Key<IntRule> = newGameRule("maxZombies", 150)

    @kotlin.jvm.JvmField
    val SPAWN_SPEED: GameRules.Key<IntRule> = newGameRule("fillZombieCapOverSeconds", 30)

    @kotlin.jvm.JvmField
    val ZOMBIES_BURN_IN_DAYLIGHT: GameRules.Key<BooleanRule> = newGameRule("zombiesBurnInDaylight", false)

    @kotlin.jvm.JvmField
    val DO_ZOMBIE_SPAWNING: GameRules.Key<BooleanRule> = newGameRule("doZombieSpawning", true)

    @kotlin.jvm.JvmStatic
    fun initialize() {}
}
