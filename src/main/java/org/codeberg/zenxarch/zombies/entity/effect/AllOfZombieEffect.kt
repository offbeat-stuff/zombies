package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity

@JvmRecord
data class AllOfZombieEffect(val effects: List<ZombieEffect>) : ZombieEffect {
    override fun run(
        world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?
    ) {
        effects.forEach { it.run(world,zombie,adversery) }
    }

    companion object {
        fun create(vararg effects: ZombieEffect?): ZombieEffect {
            return when {
                effects.isEmpty() -> DefaultZombieEffect.create()
                effects.size == 1 -> effects[0] ?: DefaultZombieEffect.create()
                else -> AllOfZombieEffect(effects.mapNotNull { it }.toList())
            }
        }
    }
}
