package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;

public record StatusLivingEffect(List<StatusEffectInstance> effects) implements LivingEffect {
  public static final MapCodec<StatusLivingEffect> CODEC =
      Codecs.listOrSingle(StatusEffectInstance.CODEC)
          .xmap(StatusLivingEffect::new, StatusLivingEffect::effects)
          .fieldOf("effects");

  public StatusLivingEffect(StatusEffectInstance effect) {
    this(List.of(effect));
  }

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var random = target.getRandom();
    var optionalEntry = Util.getRandomOrEmpty(effects, random);
    optionalEntry.ifPresent(target::addStatusEffect);
  }

  @Override
  public MapCodec<StatusLivingEffect> getCodec() {
    return CODEC;
  }
}
