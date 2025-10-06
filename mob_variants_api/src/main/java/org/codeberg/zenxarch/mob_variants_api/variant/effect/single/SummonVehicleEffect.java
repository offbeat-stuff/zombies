package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
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
    var entity = entityType.spawn(world, target.getBlockPos(), SpawnReason.JOCKEY);
    if (entity == null) return;
    target.startRiding(entity);
    if (!world.isSpaceEmpty(entity) || !world.isSpaceEmpty(target)) {
      target.stopRiding();
      entity.discard();
      return;
    }

    if (entity instanceof MobEntity mob)
      mob.initialize(
          world, world.getLocalDifficulty(target.getBlockPos()), SpawnReason.JOCKEY, null);
    if (entity instanceof ChickenEntity chicken) chicken.setHasJockey(true);
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
