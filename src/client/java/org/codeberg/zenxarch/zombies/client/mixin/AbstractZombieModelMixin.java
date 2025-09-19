package org.codeberg.zenxarch.zombies.client.mixin;

import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import org.codeberg.zenxarch.zombies.client.ExtendedZombieEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractZombieModel.class)
public abstract class AbstractZombieModelMixin {

  @SuppressWarnings("rawtypes")
  @Inject(method = "setAngles", at = @At("TAIL"))
  private void zenxarch$hideHead(ZombieEntityRenderState zstate, CallbackInfo ci) {
    if (zstate instanceof ExtendedZombieEntityRenderState exState) {
      ((AbstractZombieModel) (Object) this).getHead().hidden = !exState.renderHead;
    }
  }
}
