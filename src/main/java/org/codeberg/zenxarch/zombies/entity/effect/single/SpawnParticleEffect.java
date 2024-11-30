package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;

public record SpawnParticleEffect(
    ParticleEffect particle,
    SpawnParticlesEnchantmentEffect.PositionSource horizontalPosition,
    SpawnParticlesEnchantmentEffect.PositionSource verticalPosition,
    SpawnParticlesEnchantmentEffect.VelocitySource horizontalVelocity,
    SpawnParticlesEnchantmentEffect.VelocitySource verticalVelocity,
    FloatProvider speed)
    implements SingleLivingEffect {

  public static final MapCodec<SpawnParticleEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      ParticleTypes.TYPE_CODEC
                          .fieldOf("particle")
                          .forGetter(SpawnParticleEffect::particle),
                      SpawnParticlesEnchantmentEffect.PositionSource.CODEC
                          .fieldOf("horizontal_position")
                          .forGetter(SpawnParticleEffect::horizontalPosition),
                      SpawnParticlesEnchantmentEffect.PositionSource.CODEC
                          .fieldOf("vertical_position")
                          .forGetter(SpawnParticleEffect::verticalPosition),
                      SpawnParticlesEnchantmentEffect.VelocitySource.CODEC
                          .fieldOf("horizontal_velocity")
                          .forGetter(SpawnParticleEffect::horizontalVelocity),
                      SpawnParticlesEnchantmentEffect.VelocitySource.CODEC
                          .fieldOf("vertical_velocity")
                          .forGetter(SpawnParticleEffect::verticalVelocity),
                      FloatProvider.VALUE_CODEC
                          .optionalFieldOf("speed", ConstantFloatProvider.ZERO)
                          .forGetter(SpawnParticleEffect::speed))
                  .apply(instance, SpawnParticleEffect::new));

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

  @Override
  public MapCodec<? extends SingleLivingEffect> getCodec() {
    return CODEC;
  }
}
