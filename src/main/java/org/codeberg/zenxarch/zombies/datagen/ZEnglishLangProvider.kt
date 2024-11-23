package org.codeberg.zenxarch.zombies.datagen

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.registry.RegistryWrapper.WrapperLookup
import net.minecraft.world.GameRules
import org.codeberg.zenxarch.zombies.ZombieGamerules
import java.util.concurrent.CompletableFuture

class ZEnglishLangProvider(
    dataOutput: FabricDataOutput?,
    registryLookup: CompletableFuture<WrapperLookup?>?
) :
    FabricLanguageProvider(dataOutput, "en_us", registryLookup) {
    override fun generateTranslations(
        registryLookup: WrapperLookup, translationBuilder: TranslationBuilder
    ) {
        addGameruleTranslation(
            translationBuilder,
            ZombieGamerules.MAX_ZOMBIES,
            "Maximum zombies per player",
            "The max number of zombies that can spawn in a 80 block radius around player"
        )
        addGameruleTranslation(
            translationBuilder,
            ZombieGamerules.SPAWN_SPEED,
            "Fill zombie cap over seconds",
            "The mod tries to spawn the target amount of zombies (difficulty dependent) over this many"
                    + " seconds assuming 1% of positions are spawnable"
        )
        addGameruleTranslation(
            translationBuilder,
            ZombieGamerules.ZOMBIES_BURN_IN_DAYLIGHT,
            "Zombies burn in daylight",
            "Should zombies burn in (and not spawn in) daylight. (only affects mod's zombies)"
        )
        addGameruleTranslation(
            translationBuilder,
            ZombieGamerules.DO_ZOMBIE_SPAWNING,
            "Spawn zombies",
            "Controls whether to spawn zombies (modded) or not"
        )
    }

    companion object {
        fun addGameruleTranslation(
            translationBuilder: TranslationBuilder,
            gamerule: GameRules.Key<*>,
            name: String,
            description: String
        ) {
            translationBuilder.add(gamerule.translationKey, name)
            translationBuilder.add(gamerule.translationKey + ".description", description)
        }
    }
}
