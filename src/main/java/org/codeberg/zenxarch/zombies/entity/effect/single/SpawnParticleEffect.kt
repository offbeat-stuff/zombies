package org.codeberg.zenxarch.zombies.entity.effect.single

import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect.VelocitySource
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.floatprovider.FloatProvider

@JvmRecord
data class SpawnParticleEffect(
    val particle: ParticleEffect,
    val horizontalPosition: SpawnParticlesEnchantmentEffect.PositionSource,
    val verticalPosition: SpawnParticlesEnchantmentEffect.PositionSource,
    val horizontalVelocity: VelocitySource,
    val verticalVelocity: VelocitySource,
    val speed: FloatProvider
) : SingleLivingEffect {
    override fun run(world: ServerWorld, target: LivingEntity) {
        val random = target.random
        val movement = target.movement
        val width = target.width
        val height = target.height
        val pos = target.pos
        world.spawnParticles(
            this.particle,
            horizontalPosition.getPosition(pos.getX(), pos.getX(), width, random),
            verticalPosition.getPosition(
                pos.getY(), pos.getY() + (height / 2.0f).toDouble(), height, random
            ),
            horizontalPosition.getPosition(pos.getZ(), pos.getZ(), width, random),
            0,
            horizontalVelocity.getVelocity(movement.getX(), random),
            verticalVelocity.getVelocity(movement.getY(), random),
            horizontalVelocity.getVelocity(movement.getZ(), random),
            speed[random].toDouble()
        )
    }
}
