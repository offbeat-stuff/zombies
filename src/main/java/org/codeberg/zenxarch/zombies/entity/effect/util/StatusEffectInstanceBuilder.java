package org.codeberg.zenxarch.zombies.entity.effect.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;

public record StatusEffectInstanceBuilder(
    RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
  public static final MapCodec<StatusEffectInstanceBuilder> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      StatusEffect.ENTRY_CODEC
                          .fieldOf("effect")
                          .forGetter(StatusEffectInstanceBuilder::effect),
                      Codecs.POSITIVE_INT
                          .fieldOf("duration")
                          .forGetter(StatusEffectInstanceBuilder::duration),
                      Codecs.POSITIVE_INT
                          .fieldOf("amplifier")
                          .forGetter(StatusEffectInstanceBuilder::amplifier))
                  .apply(instance, StatusEffectInstanceBuilder::new));

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
