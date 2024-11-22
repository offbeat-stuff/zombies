package org.codeberg.zenxarch.zombies.entity.effect.single;

import java.util.List;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.effect.util.StatusEffectInstanceBuilder;

public record SpawnEffectCloudEffect(
    List<StatusEffectInstanceBuilder> effects,
    ParticleEffect particleEffect,
    float radius,
    float radiusOnUse)
    implements SingleLivingEffect {

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    if (effects.size() == 0) return;
    var cloud = new AreaEffectCloudEntity(world, target.getX(), target.getY(), target.getZ());
    cloud.setRadius(radius);
    cloud.setRadiusOnUse(radiusOnUse);
    cloud.setWaitTime(10);
    cloud.setDuration(cloud.getDuration() / 2);
    cloud.setRadiusGrowth(-cloud.getRadius() / (float) cloud.getDuration());

    cloud.setParticleType(particleEffect);

    for (var effect : effects) cloud.addEffect(effect.build());

    world.spawnEntity(cloud);
  }
}
