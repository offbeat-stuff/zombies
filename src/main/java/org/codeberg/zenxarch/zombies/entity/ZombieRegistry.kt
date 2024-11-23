package org.codeberg.zenxarch.zombies.entity

import com.google.common.collect.ImmutableMap
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.random.Random
import net.minecraft.world.biome.Biome
import java.util.*

object ZombieRegistry {
    @JvmStatic
    val defaultId: String
        get() = "BaseZombie"

    private var registry: MutableMap<String, ZombieTemplate> = HashMap()

    @JvmStatic
    fun register(id: String, template: ZombieTemplate) {
        registry[id] = template
    }

    @JvmStatic
    fun freezeRegistry() {
        registry = ImmutableMap.Builder<String, ZombieTemplate>().putAll(registry).build()
    }

    fun getTemplate(id: String): ZombieTemplate {
        return when {
            !registry.containsKey(id) -> default
            else -> registry[id]!!
        }
    }

    fun templates(): Collection<ZombieTemplate> {
        return registry.values
    }

    fun getId(template: ZombieTemplate): String {
        for ((key, value) in registry) {
            if (value == template) return key
        }
        return defaultId
    }

    val default: ZombieTemplate
        get() = registry[defaultId]!!

    fun newZombie(
        world: ServerWorld, template: ZombieTemplate, pos: BlockPos?
    ): Optional<ExtendedZombieEntity> {
        val zombie = ExtendedZombieEntity(world, template)
        zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0f, 0.0f)
        if (zombie.canSpawn(world)) return Optional.of(zombie)
        return Optional.empty()
    }

    fun selectTemplate(random: Random, biome: RegistryEntry<Biome>): Optional<ZombieTemplate> {
        val templates =
            templates().stream()
                .filter { t: ZombieTemplate -> t.weight > 0 }
                .filter { t: ZombieTemplate -> t.canSpawnIn(biome) }
                .toList()
        if (templates.isEmpty()) return Optional.empty()
        val totalWeight = templates.stream().mapToInt { obj: ZombieTemplate -> obj.weight }.sum()
        var selection = random.nextInt(totalWeight)
        for (t in templates) {
            if (selection < t.weight) return Optional.of(t)
            selection -= t.weight
        }
        return Optional.of<ZombieTemplate>(templates.last())
    }
}
