package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin {

  @Inject(at = @At("HEAD"), method = "burnsInDaylight", cancellable = true)
  private void dontBurnInDaylight(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(false);
  }
}
