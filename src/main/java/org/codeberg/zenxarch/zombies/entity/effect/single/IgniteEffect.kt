package org.codeberg.zenxarch.zombies.entity.effect.single

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld

@JvmRecord
data class IgniteEffect(val seconds: Float) : SingleLivingEffect {
    override fun run(world: ServerWorld, target: LivingEntity) {
        if (target.isFireImmune) return
        target.setOnFireFor(seconds)
    }
}
