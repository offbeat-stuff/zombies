package org.codeberg.zenxarch.mob_variants_api.variant.effect.pair;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.mob_variants_api.mixin.EntityConversionTypeInvoker;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record ConvertToEntityTypeEffect(EntityType<?> type) implements MobEffect {

  public static final MapCodec<ConvertToEntityTypeEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      EntityType.CODEC
                          .fieldOf("entityType")
                          .forGetter(ConvertToEntityTypeEffect::type))
                  .apply(instance, ConvertToEntityTypeEffect::new));

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (mob.isRemoved()) return;

    var entity = type.create(world, SpawnReason.CONVERSION);
    if (entity == null) return;

    if (!(entity instanceof MobEntity newMob)) return;

    var context = EntityConversionContext.create(mob, true, true);

    ((EntityConversionTypeInvoker) (Object) context.type())
        .zenxarch$setUpNewEntity(mob, newMob, context);
    world.spawnEntity(newMob);

    if (context.type().shouldDiscardOldEntity()) mob.discard();
  }

  @Override
  public MapCodec<? extends MobEffect> getCodec() {
    return CODEC;
  }

  public static ConvertToEntityTypeEffect create(EntityType<? extends MobEntity> type) {
    return new ConvertToEntityTypeEffect(type);
  }
}
