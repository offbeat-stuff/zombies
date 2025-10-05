package org.codeberg.zenxarch.mob_variants_api.variant.effect.single;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.LivingEffect;

public record AttributeModifierEffect(
    RegistryEntry<EntityAttribute> attribute,
    List<EntityAttributeModifier> modifiers,
    boolean persistent)
    implements LivingEffect {
  public static final MapCodec<AttributeModifierEffect> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      EntityAttribute.CODEC
                          .fieldOf("attribute")
                          .forGetter(AttributeModifierEffect::attribute),
                      Codecs.listOrSingle(EntityAttributeModifier.CODEC)
                          .fieldOf("modifiers")
                          .forGetter(AttributeModifierEffect::modifiers),
                      Codec.BOOL
                          .optionalFieldOf("persistent", true)
                          .forGetter(AttributeModifierEffect::persistent))
                  .apply(instance, AttributeModifierEffect::new));

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var instance = target.getAttributeInstance(attribute);
    if (instance == null) return;
    modifiers.forEach(modifier -> addModifier(instance, modifier));
  }

  private void addModifier(EntityAttributeInstance instance, EntityAttributeModifier modifier) {
    if (instance.hasModifier(modifier.id())) return;
    if (persistent) instance.addPersistentModifier(modifier);
    else instance.addTemporaryModifier(modifier);
  }

  @Override
  public MapCodec<? extends LivingEffect> getCodec() {
    return CODEC;
  }
}
