package org.codeberg.zenxarch.zombies.entity.effect.single;

import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.floatprovider.FloatProvider;

public record SpawnParticleEffect(
    ParticleEffect particle,
    SpawnParticlesEnchantmentEffect.PositionSource horizontalPosition,
    SpawnParticlesEnchantmentEffect.PositionSource verticalPosition,
    SpawnParticlesEnchantmentEffect.VelocitySource horizontalVelocity,
    SpawnParticlesEnchantmentEffect.VelocitySource verticalVelocity,
    FloatProvider speed)
    implements SingleLivingEffect {

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var random = target.getRandom();
    var movement = target.getMovement();
    var width = target.getWidth();
    var height = target.getHeight();
    var pos = target.getPos();
    world.spawnParticles(
        this.particle,
        this.horizontalPosition.getPosition(pos.getX(), pos.getX(), width, random),
        this.verticalPosition.getPosition(
            pos.getY(), pos.getY() + (double) (height / 2.0F), height, random),
        this.horizontalPosition.getPosition(pos.getZ(), pos.getZ(), width, random),
        0,
        this.horizontalVelocity.getVelocity(movement.getX(), random),
        this.verticalVelocity.getVelocity(movement.getY(), random),
        this.horizontalVelocity.getVelocity(movement.getZ(), random),
        (double) this.speed.get(random));
  }
}
