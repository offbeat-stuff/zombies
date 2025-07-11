package org.codeberg.zenxarch.zombies.data.entity.effect.single;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;

public record SpawnEffectCloudEffect(
    List<StatusEffectInstance> effects,
    ParticleEffect particleEffect,
    float radius,
    float radiusOnUse,
    int duration)
    implements LivingEffect {

  public static final MapCodec<SpawnEffectCloudEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      StatusEffectInstance.CODEC
                          .listOf(1, Integer.MAX_VALUE)
                          .fieldOf("effects")
                          .forGetter(SpawnEffectCloudEffect::effects),
                      ParticleTypes.TYPE_CODEC
                          .fieldOf("particleEffect")
                          .forGetter(SpawnEffectCloudEffect::particleEffect),
                      Codec.FLOAT.fieldOf("radius").forGetter(SpawnEffectCloudEffect::radius),
                      Codec.FLOAT
                          .fieldOf("radiusOnUse")
                          .forGetter(SpawnEffectCloudEffect::radiusOnUse),
                      Codec.INT.fieldOf("duration").forGetter(SpawnEffectCloudEffect::duration))
                  .apply(instance, SpawnEffectCloudEffect::new));

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    if (effects.isEmpty()) return;
    var cloud = new AreaEffectCloudEntity(world, target.getX(), target.getY(), target.getZ());
    cloud.setRadius(radius);
    cloud.setRadiusOnUse(radiusOnUse);
    cloud.setWaitTime(10);
    cloud.setDuration(this.duration);
    cloud.setRadiusGrowth(-cloud.getRadius() / cloud.getDuration());

    cloud.setParticleType(particleEffect);

    for (var effect : effects) cloud.addEffect(effect);

    world.spawnEntity(cloud);
  }

  @Override
  public MapCodec<SpawnEffectCloudEffect> getCodec() {
    return CODEC;
  }
}
