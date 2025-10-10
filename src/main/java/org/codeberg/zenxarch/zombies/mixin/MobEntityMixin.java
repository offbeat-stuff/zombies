package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.entity.mob.MobEntity;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

  @Inject(method = "method_75128", at = @At("HEAD"), cancellable = true)
  private void zenxarch$modifyBurnInDaylight(CallbackInfo ci) {
    if (((Object) this) instanceof ExtendedZombieEntity zombie && !zombie.burnsInDaylight())
      ci.cancel();
  }
}
