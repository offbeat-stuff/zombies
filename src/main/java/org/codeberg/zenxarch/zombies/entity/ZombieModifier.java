package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.floatprovider.FloatProvider;

public interface ZombieModifier {

  public void apply(ExtendedZombieEntity zombie);

  public static record DefaultAttribute(
      RegistryEntry<EntityAttribute> attribute, FloatProvider value) implements ZombieModifier {
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

    @Override
    public void apply(ExtendedZombieEntity zombie) {
      zombie.getAttributeInstance(attribute).setBaseValue(value.get(zombie.getRandom()));
    }
  }

  public static record AttributeModifier(
      RegistryEntry<EntityAttribute> attribute, EntityAttributeModifier modifier)
      implements ZombieModifier {
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

    @Override
    public void apply(ExtendedZombieEntity zombie) {
      zombie.getAttributeInstance(attribute).addPersistentModifier(modifier);
    }
  }
}
