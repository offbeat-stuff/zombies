package org.codeberg.zenxarch.zombies.entity.effect.single

import net.minecraft.entity.AreaEffectCloudEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld
import org.codeberg.zenxarch.zombies.entity.effect.util.StatusEffectInstanceBuilder

@JvmRecord
data class SpawnEffectCloudEffect(
    val effects: List<StatusEffectInstanceBuilder>,
    val particleEffect: ParticleEffect,
    val radius: Float,
    val radiusOnUse: Float
) : SingleLivingEffect {
    override fun run(world: ServerWorld, target: LivingEntity) {
        if (effects.isEmpty()) return
        val cloud = AreaEffectCloudEntity(world, target.x, target.y, target.z)
        cloud.radius = radius
        cloud.radiusOnUse = radiusOnUse
        cloud.waitTime = 10
        cloud.duration /= 2
        cloud.radiusGrowth = -cloud.radius / cloud.duration.toFloat()

        cloud.particleType = particleEffect

        for (effect in effects) cloud.addEffect(effect.build())

        world.spawnEntity(cloud)
    }
}
