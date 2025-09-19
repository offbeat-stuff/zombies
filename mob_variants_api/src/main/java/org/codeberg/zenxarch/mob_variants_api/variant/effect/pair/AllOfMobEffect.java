package org.codeberg.zenxarch.mob_variants_api.variant.effect.pair;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record AllOfMobEffect(List<MobEffect> effects) implements MobEffect {

  public static final MapCodec<AllOfMobEffect> CODEC =
      Codecs.listOrSingle(MobEffect.CODEC)
          .xmap(AllOfMobEffect::new, AllOfMobEffect::effects)
          .fieldOf("effects");

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    for (MobEffect effect : effects) effect.run(world, mob, adversery);
  }

  @Override
  public MapCodec<AllOfMobEffect> getCodec() {
    return CODEC;
  }

  public static MobEffect create(MobEffect... effects) {
    if (effects.length == 0) return DefaultMobEffect.create();
    if (effects.length == 1) return effects[0];
    return new AllOfMobEffect(List.of(effects));
  }
}
