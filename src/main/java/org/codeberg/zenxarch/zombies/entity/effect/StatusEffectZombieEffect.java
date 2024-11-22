package org.codeberg.zenxarch.zombies.entity.effect;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.effect.util.StatusEffectInstanceBuilder;
import org.jetbrains.annotations.Nullable;

public record StatusEffectZombieEffect(List<StatusEffectInstanceBuilder> effects)
    implements ZombieEffect {

  public StatusEffectZombieEffect(StatusEffectInstanceBuilder effect) {
    this(List.of(effect));
  }

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    var random = zombie.getRandom();
    var optionalEntry = Util.getRandomOrEmpty(effects, random);
    if (optionalEntry.isPresent()) adversery.addStatusEffect(optionalEntry.get().build(), zombie);
  }
}
