package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;

public record RemoveAttributeModifierEffect(
    RegistryEntry<EntityAttribute> attribute, List<EntityAttributeModifier> modifiers)
    implements LivingEffect {

  public static final MapCodec<RemoveAttributeModifierEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      EntityAttribute.CODEC
                          .fieldOf("attribute")
                          .forGetter(RemoveAttributeModifierEffect::attribute),
                      Codecs.listOrSingle(EntityAttributeModifier.CODEC)
                          .fieldOf("modifiers")
                          .forGetter(RemoveAttributeModifierEffect::modifiers))
                  .apply(instance, RemoveAttributeModifierEffect::new));

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var instance = target.getAttributeInstance(attribute);
    if (instance == null) return;
    for (var modifier : modifiers) instance.removeModifier(modifier);
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
