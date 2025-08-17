package org.codeberg.zenxarch.zombies.variant.effect.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public interface StatusEffectInstanceBuilder {

  public static StatusEffectInstance createInfinite(RegistryEntry<StatusEffect> effect) {
    return new StatusEffectInstance(effect, -1, 1);
  }

  public static StatusEffectInstance createInfinite(
      RegistryEntry<StatusEffect> effect, int amplifier) {
    return new StatusEffectInstance(effect, -1, amplifier);
  }

  public static StatusEffectInstance create(RegistryEntry<StatusEffect> effect, int duration) {
    return new StatusEffectInstance(effect, duration, 1);
  }

  public static StatusEffectInstance create(
      RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
    return new StatusEffectInstance(effect, duration, amplifier);
  }
}
