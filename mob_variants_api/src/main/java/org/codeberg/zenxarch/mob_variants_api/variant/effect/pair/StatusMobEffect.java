package org.codeberg.zenxarch.mob_variants_api.variant.effect.pair;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record StatusMobEffect(List<StatusEffectInstance> effects) implements MobEffect {

  public static final MapCodec<StatusMobEffect> CODEC =
      Codecs.listOrSingle(StatusEffectInstance.CODEC)
          .xmap(StatusMobEffect::new, StatusMobEffect::effects)
          .fieldOf("effects");

  public StatusMobEffect(StatusEffectInstance effect) {
    this(List.of(effect));
  }

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    var random = mob.getRandom();
    var optionalEntry = Util.getRandomOrEmpty(effects, random);
    if (optionalEntry.isPresent()) adversery.addStatusEffect(optionalEntry.get(), mob);
  }

  @Override
  public MapCodec<StatusMobEffect> getCodec() {
    return CODEC;
  }
}
