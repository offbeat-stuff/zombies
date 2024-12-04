package org.codeberg.zenxarch.zombies.entity.effect;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record StatusEffectZombieEffect(List<StatusEffectInstance> effects) implements ZombieEffect {

  public static final MapCodec<StatusEffectZombieEffect> CODEC =
      Codecs.listOrSingle(StatusEffectInstance.CODEC)
          .xmap(StatusEffectZombieEffect::new, StatusEffectZombieEffect::effects)
          .fieldOf("effects");

  public StatusEffectZombieEffect(StatusEffectInstance effect) {
    this(List.of(effect));
  }

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    var random = zombie.getRandom();
    var optionalEntry = Util.getRandomOrEmpty(effects, random);
    if (optionalEntry.isPresent()) adversery.addStatusEffect(optionalEntry.get(), zombie);
  }

  @Override
  public MapCodec<StatusEffectZombieEffect> getCodec() {
    return CODEC;
  }
}
