package org.codeberg.zenxarch.zombies.entity.effect.single

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld

class FreezeEffect : SingleLivingEffect {
    override fun run(world: ServerWorld, target: LivingEntity) {
        if (!target.canFreeze()) return
        target.frozenTicks = target.minFreezeDamageTicks
    }

    companion object {
        private val INSTANCE = FreezeEffect()

        fun create(): FreezeEffect {
            return INSTANCE
        }
    }
}
