package org.codeberg.zenxarch.zombies

import net.fabricmc.api.ModInitializer
import net.minecraft.util.Identifier
import org.codeberg.zenxarch.zombies.entity.ZombieTemplates
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootFunctionTypes
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootNumberProviderTypes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun init() {
    LOGGER.info("Hello Fabric world!")
    ZombieGamerules.initialize()
    ZombieLootNumberProviderTypes.initialize()
    ZombieLootFunctionTypes.initialize()
    ZombieTemplates.initialize()
}

// This logger is used to write text to the console and the log file.
// It is considered best practice to use your mod id as the logger's name.
// That way, it's clear which mod wrote info, warnings, and errors.
val LOGGER: Logger = LoggerFactory.getLogger("zombies_zenxarch")
const val DEBUG: Boolean = false

object Zombies {
    @JvmStatic
    fun id(path: String) = Identifier.of("zenxarch", path)
}