package org.codeberg.zenxarch.zombies.entity.effect.util

import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.registry.entry.RegistryEntry

@JvmRecord
data class StatusEffectInstanceBuilder(val effect: RegistryEntry<StatusEffect>, val duration: Int, val amplifier: Int) {
    fun build(): StatusEffectInstance {
        return StatusEffectInstance(effect, duration, amplifier)
    }

    companion object {
        fun createInfinite(effect: RegistryEntry<StatusEffect>) =
            StatusEffectInstanceBuilder(effect, -1, 1)

        fun createInfinite(
            effect: RegistryEntry<StatusEffect>, amplifier: Int
        ) = StatusEffectInstanceBuilder(effect, -1, amplifier)

        fun create(
            effect: RegistryEntry<StatusEffect>, duration: Int
        ) = StatusEffectInstanceBuilder(effect, duration, 1)

        fun create(
            effect: RegistryEntry<StatusEffect>, duration: Int, amplifier: Int
        ) = StatusEffectInstanceBuilder(effect, duration, amplifier)
    }
}
