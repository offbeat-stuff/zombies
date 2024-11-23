package org.codeberg.zenxarch.zombies.entity.effect

import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect.VelocitySource
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.floatprovider.ConstantFloatProvider
import net.minecraft.util.math.floatprovider.FloatProvider
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity
import org.codeberg.zenxarch.zombies.entity.effect.RandomZombieEffect.Companion.create
import org.codeberg.zenxarch.zombies.entity.effect.single.FreezeEffect
import org.codeberg.zenxarch.zombies.entity.effect.single.IgniteEffect
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect
import org.codeberg.zenxarch.zombies.entity.effect.single.SpawnParticleEffect
import org.codeberg.zenxarch.zombies.entity.effect.util.StatusEffectInstanceBuilder
import java.util.List

interface ZombieEffect {
    fun run(world: ServerWorld, zombie: ExtendedZombieEntity, adversery: LivingEntity?)

    companion object {
        fun swapPositions(): SwapPositionZombieEffect {
            return SwapPositionZombieEffect.create()
        }

        fun toPlayer(effect: SingleLivingEffect): SingleTargetZombieEffect {
            return SingleTargetZombieEffect(effect, false)
        }

        fun toZombie(effect: SingleLivingEffect): SingleTargetZombieEffect {
            return SingleTargetZombieEffect(effect, true)
        }

        fun ignite(seconds: Float): SingleTargetZombieEffect {
            return toPlayer(IgniteEffect(seconds))
        }

        fun freeze(): SingleTargetZombieEffect {
            return toPlayer(FreezeEffect.create())
        }

        fun random(vararg effect: ZombieEffect?): ZombieEffect {
            return create(*effect)
        }

        fun statusEffect(vararg builders: StatusEffectInstanceBuilder?): ZombieEffect {
            if (builders.isEmpty()) return DefaultZombieEffect.create()
            return StatusEffectZombieEffect(builders.mapNotNull { it }.toList())
        }

        fun infiniteStatusEffectBuilder(
            entry: RegistryEntry<StatusEffect>
        ): StatusEffectInstanceBuilder {
            return StatusEffectInstanceBuilder.createInfinite(entry)
        }

        fun statusEffectBuilder(
            entry: RegistryEntry<StatusEffect>, duration: Int
        ) = StatusEffectInstanceBuilder.create(entry, duration)

        fun spawnParticles(
            particle: ParticleEffect,
            horizontalPosition: SpawnParticlesEnchantmentEffect.PositionSource,
            verticalPosition: SpawnParticlesEnchantmentEffect.PositionSource,
            horizontalVelocity: VelocitySource,
            verticalVelocity: VelocitySource,
            speed: FloatProvider
        ): SingleTargetZombieEffect {
            return toZombie(
                SpawnParticleEffect(
                    particle,
                    horizontalPosition,
                    verticalPosition,
                    horizontalVelocity,
                    verticalVelocity,
                    speed
                )
            )
        }

        fun spawnParticles(particle: ParticleEffect, speed: Float): SingleTargetZombieEffect {
            val floatProvider = ConstantFloatProvider.create(speed)
            return spawnParticles(
                particle,
                SpawnParticlesEnchantmentEffect.withinBoundingBox(),
                SpawnParticlesEnchantmentEffect.withinBoundingBox(),
                SpawnParticlesEnchantmentEffect.fixedVelocity(floatProvider),
                SpawnParticlesEnchantmentEffect.fixedVelocity(floatProvider),
                floatProvider
            )
        }
    }
}
