package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record SpawnEffectCloudZombieEffect(
    RegistryEntryList<StatusEffect> effects,
    int duration,
    int amplifier,
    float radius,
    float radiusOnUse)
    implements ZombieEffect {

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (effects.size() == 0) return;
    var cloud = new AreaEffectCloudEntity(world, zombie.getX(), zombie.getY(), zombie.getZ());
    cloud.setRadius(radius);
    cloud.setRadiusOnUse(radiusOnUse);
    cloud.setWaitTime(10);
    cloud.setDuration(cloud.getDuration() / 2);
    cloud.setRadiusGrowth(-cloud.getRadius() / (float) cloud.getDuration());
    for (var effect : effects)
      cloud.addEffect(new StatusEffectInstance(effect, duration, amplifier));

    world.spawnEntity(cloud);
  }
}
