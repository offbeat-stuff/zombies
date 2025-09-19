package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.floatprovider.FloatProvider;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;

public record DefaultAttributeEffect(RegistryEntry<EntityAttribute> attribute, FloatProvider value)
    implements LivingEffect {
  public static final MapCodec<DefaultAttributeEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      EntityAttribute.CODEC
                          .fieldOf("attribute")
                          .forGetter(DefaultAttributeEffect::attribute),
                      FloatProvider.VALUE_CODEC
                          .fieldOf("value")
                          .forGetter(DefaultAttributeEffect::value))
                  .apply(instance, DefaultAttributeEffect::new));

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    target.getAttributeInstance(attribute).setBaseValue(value.get(target.getRandom()));
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
