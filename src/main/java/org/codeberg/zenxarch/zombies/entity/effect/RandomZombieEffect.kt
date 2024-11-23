package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity

@JvmRecord
data class RandomZombieEffect(val effects: List<ZombieEffect>) : ZombieEffect {
    override fun run(
        world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?
    ) {
        Util.getRandomOrEmpty(effects, zombie.random)
            .ifPresent { effect: ZombieEffect -> effect.run(world, zombie, adversery) }
    }

    companion object {
        @JvmStatic
        fun create(vararg effects: ZombieEffect?): ZombieEffect {
            return when {
                effects.isEmpty() -> DefaultZombieEffect.create()
                effects.size == 1 -> effects[0] ?: DefaultZombieEffect.create()
                else -> RandomZombieEffect(effects.mapNotNull { it }.toList())
            }
        }
    }
}
