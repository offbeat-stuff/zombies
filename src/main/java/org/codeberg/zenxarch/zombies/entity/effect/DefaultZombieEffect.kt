package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity

class DefaultZombieEffect : ZombieEffect {
    override fun run(
        world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?
    ) {
    }

    companion object {
        private val INSTANCE = DefaultZombieEffect()

        @JvmStatic
        fun create(): DefaultZombieEffect {
            return INSTANCE
        }
    }
}
