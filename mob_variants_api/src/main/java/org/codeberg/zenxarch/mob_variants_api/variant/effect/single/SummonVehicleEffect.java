package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;

public record SummonVehicleEffect(EntityType<?> entityType) implements LivingEffect {
  public static final MapCodec<SummonVehicleEffect> CODEC =
      EntityType.CODEC
          .xmap(SummonVehicleEffect::new, SummonVehicleEffect::entityType)
          .fieldOf("entityType");

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    // todo: implement jockey
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
