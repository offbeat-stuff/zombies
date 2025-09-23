package org.codeberg.zenxarch.mob_variants_api.client.mixin;

import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import org.codeberg.zenxarch.mob_variants_api.client.ExtendedRenderStateKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin {
  @SuppressWarnings("rawtypes")
  @Inject(method = "setAngles", at = @At("TAIL"))
  private void zenxarch$hideHead(BipedEntityRenderState zstate, CallbackInfo ci) {
    ((BipedEntityModel) (Object) this).getHead().hidden =
        !ExtendedRenderStateKeys.getRenderHead(zstate);
  }
}
