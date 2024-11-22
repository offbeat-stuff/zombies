package org.codeberg.zenxarch.zombies.entity.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.jetbrains.annotations.Nullable;

public record StatusEffectZombieEffect(
    RegistryEntryList<StatusEffect> effects, int duration, int amplifier) implements ZombieEffect {

  public StatusEffectZombieEffect(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
    this(RegistryEntryList.of(effect), duration, amplifier);
  }

  @Override
  public void run(
      ServerWorld world, ExtendedZombieEntity zombie, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    var random = zombie.getRandom();
    var optionalEntry = effects.getRandom(random);
    if (optionalEntry.isPresent())
      adversery.addStatusEffect(
          new StatusEffectInstance(optionalEntry.get(), duration, amplifier), zombie);
  }
}
