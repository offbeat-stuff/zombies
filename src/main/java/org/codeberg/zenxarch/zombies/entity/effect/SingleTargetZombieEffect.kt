package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect

@JvmRecord
data class SingleTargetZombieEffect(val effect: SingleLivingEffect, val targetZombie: Boolean) : ZombieEffect {
    override fun run(
        world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?
    ) {
        when {
            targetZombie -> effect.run(world, zombie)
            adversery != null -> effect.run(world, adversery)
        }
    }
}
