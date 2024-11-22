package org.codeberg.zenxarch.zombies.entity.effect.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;

public record StatusEffectInstanceBuilder(
    RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
  public static StatusEffectInstanceBuilder createInfinite(RegistryEntry<StatusEffect> effect) {
    return new StatusEffectInstanceBuilder(effect, -1, 1);
  }

  public static StatusEffectInstanceBuilder createInfinite(
      RegistryEntry<StatusEffect> effect, int amplifier) {
    return new StatusEffectInstanceBuilder(effect, -1, amplifier);
  }

  public static StatusEffectInstanceBuilder create(
      RegistryEntry<StatusEffect> effect, int duration) {
    return new StatusEffectInstanceBuilder(effect, duration, 1);
  }

  public static StatusEffectInstanceBuilder create(
      RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
    return new StatusEffectInstanceBuilder(effect, duration, amplifier);
  }

  public StatusEffectInstance build() {
    return new StatusEffectInstance(effect, duration, amplifier);
  }
}
