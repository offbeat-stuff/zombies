package org.codeberg.zenxarch.zombies.entity.effect.single

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld

interface SingleLivingEffect {
    fun run(world: ServerWorld, target: LivingEntity)
}
