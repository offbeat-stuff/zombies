package org.codeberg.zenxarch.mob_variants_api.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
  @Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
  public void zenxarch$implement_damage_invulnerability(
      ServerWorld world, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
    var living = (LivingEntity) (Object) this;
    if (!living.hasAttached(MobAttachments.INVULNERABLE_TO)) return;
    var damageTypes = living.getAttached(MobAttachments.INVULNERABLE_TO);
    if (damageTypes.contains(source.getTypeRegistryEntry())) cir.setReturnValue(true);
  }
}
