package org.codeberg.zenxarch.mob_variants_api.mixin;

import net.minecraft.entity.conversion.EntityConversionContext;
import net.minecraft.entity.conversion.EntityConversionType;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityConversionType.class)
public interface EntityConversionTypeInvoker {
  @Invoker("setUpNewEntity")
  void zenxarch$setUpNewEntity(
      MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context);
}
