package org.codeberg.zenxarch.zombies.entity.effect.single;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public record StatusEffectLivingEffect(List<StatusEffectInstance> effects)
    implements SingleLivingEffect {
  public static final MapCodec<StatusEffectLivingEffect> CODEC =
      Codecs.listOrSingle(StatusEffectInstance.CODEC)
          .xmap(StatusEffectLivingEffect::new, StatusEffectLivingEffect::effects)
          .fieldOf("effects");

  public StatusEffectLivingEffect(StatusEffectInstance effect) {
    this(List.of(effect));
  }

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var random = target.getRandom();
    var optionalEntry = Util.getRandomOrEmpty(effects, random);
    if (optionalEntry.isPresent()) target.addStatusEffect(optionalEntry.get());
  }

  @Override
  public MapCodec<? extends SingleLivingEffect> getCodec() {
    return CODEC;
  }
}
