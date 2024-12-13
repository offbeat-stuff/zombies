package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProvider;

public record ZombieAttributes(List<DefaultAttribute> defaults, List<AttributeModifier> modifiers) {

  public static final Codec<ZombieAttributes> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Codecs.listOrSingle(DefaultAttribute.CODEC)
                          .optionalFieldOf("defaults", List.of())
                          .forGetter(ZombieAttributes::defaults),
                      Codecs.listOrSingle(AttributeModifier.CODEC)
                          .optionalFieldOf("modifiers", List.of())
                          .forGetter(ZombieAttributes::modifiers))
                  .apply(instance, ZombieAttributes::new));

  public static final ZombieAttributes DEFAULT = new ZombieAttributes(List.of(), List.of());

  public void applyDefaults(ExtendedZombieEntity zombie) {
    for (var def : defaults)
      zombie.getAttributeInstance(def.attribute).setBaseValue(def.value.get(zombie.getRandom()));
  }

  public void applyModifiers(ExtendedZombieEntity zombie) {
    for (var modifier : modifiers)
      zombie.getAttributeInstance(modifier.attribute).addPersistentModifier(modifier.modifier);
  }

  public static record DefaultAttribute(
      RegistryEntry<EntityAttribute> attribute, FloatProvider value) {
    public static final Codec<DefaultAttribute> CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        EntityAttribute.CODEC
                            .fieldOf("type")
                            .forGetter(DefaultAttribute::attribute),
                        FloatProvider.VALUE_CODEC
                            .fieldOf("value")
                            .forGetter(DefaultAttribute::value))
                    .apply(instance, DefaultAttribute::new));
  }

  public static record AttributeModifier(
      RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier) {
    public static final Codec<AttributeModifier> CODEC =
        RecordCodecBuilder.create(
            instance ->
                instance
                    .group(
                        EntityAttribute.CODEC
                            .fieldOf("type")
                            .forGetter(AttributeModifier::attribute),
                        EntityAttributeModifier.MAP_CODEC.forGetter(AttributeModifier::modifier))
                    .apply(instance, AttributeModifier::new));
  }
}
