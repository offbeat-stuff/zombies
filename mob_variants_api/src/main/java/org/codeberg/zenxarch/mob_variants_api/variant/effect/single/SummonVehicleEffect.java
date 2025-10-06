package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;

public record SummonVehicleEffect(EntityType<?> entityType) implements LivingEffect {
  public static final MapCodec<SummonVehicleEffect> CODEC =
      EntityType.CODEC
          .xmap(SummonVehicleEffect::new, SummonVehicleEffect::entityType)
          .fieldOf("entityType");

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var beforePos = target.getEntityPos();
    var beforeYaw = target.getYaw();
    var beforePitch = target.getPitch();
    var entity =
        entityType.create(world, null, target.getBlockPos(), SpawnReason.JOCKEY, false, false);
    if (entity == null) return;
    if (!(entity instanceof LivingEntity living)) return;
    target.startRiding(living);
    if (!doesMobFit(world, target) || !doesMobFit(world, living)) {
      target.stopRiding();
      target.refreshPositionAndAngles(beforePos, beforeYaw, beforePitch);
      return;
    }

    world.spawnEntity(living);
    if (living instanceof ChickenEntity chicken) chicken.setHasJockey(true);
  }

  private boolean doesMobFit(ServerWorld world, LivingEntity entity) {
    return world.isSpaceEmpty(entity.getBoundingBox())
        && !world.containsFluid(entity.getBoundingBox())
        && world.doesNotIntersectEntities(entity);
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
