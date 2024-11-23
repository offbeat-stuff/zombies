package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.TeleportTarget
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity

class SwapPositionZombieEffect : ZombieEffect {
    override fun run(
        world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?
    ) {
        if (adversery == null) return

        val toAdversary = getTeleportTarget(world, adversery)
        val toZombie = getTeleportTarget(world, zombie)

        zombie.teleportTo(toAdversary)
        adversery.teleportTo(toZombie)
    }

    companion object {
        private val INSTANCE = SwapPositionZombieEffect()

        private fun getTeleportTarget(world: ServerWorld, living: LivingEntity): TeleportTarget {
            return TeleportTarget(
                world,
                living.pos,
                living.velocity,
                living.yaw,
                living.pitch,
                TeleportTarget.NO_OP
            )
        }

        @JvmStatic
        fun create(): SwapPositionZombieEffect {
            return INSTANCE
        }
    }
}
