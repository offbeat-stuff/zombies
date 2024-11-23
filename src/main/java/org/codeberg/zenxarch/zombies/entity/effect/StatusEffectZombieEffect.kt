package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.entity.LivingEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Util
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity
import org.codeberg.zenxarch.zombies.entity.effect.util.StatusEffectInstanceBuilder

@JvmRecord
data class StatusEffectZombieEffect(val effects: List<StatusEffectInstanceBuilder>) : ZombieEffect {
    constructor(effect: StatusEffectInstanceBuilder) : this(listOf<StatusEffectInstanceBuilder>(effect))

    override fun run(
        world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?
    ) {
        if (adversery == null) return
        val random = zombie.random
        val optionalEntry = Util.getRandomOrEmpty(effects, random)
        if (optionalEntry.isPresent) adversery.addStatusEffect(optionalEntry.get().build(), zombie)
    }
}
