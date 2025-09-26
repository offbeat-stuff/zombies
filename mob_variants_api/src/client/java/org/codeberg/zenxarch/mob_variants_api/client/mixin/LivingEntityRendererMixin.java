package org.codeberg.zenxarch.mob_variants_api.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.mob_variants_api.client.ExtendedRenderStateDataKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

  @WrapOperation(
      method = "getRenderLayer",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/render/entity/LivingEntityRenderer;getTexture(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;)Lnet/minecraft/util/Identifier;"))
  private Identifier zenxarch$modifyTexture(
      LivingEntityRenderer<?, ?, ?> self,
      LivingEntityRenderState renderState,
      Operation<Identifier> op) {
    return ExtendedRenderStateDataKeys.TEXTURE_OVERRIDE
        .get(renderState)
        .orElse(op.call(self, renderState));
  }

  @Inject(
      method =
          "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V",
      at = @At("HEAD"))
  private void zenxarch$tryGetTextureOverride(
      LivingEntity livingEntity,
      LivingEntityRenderState livingEntityRenderState,
      float f,
      CallbackInfo ci) {
    ExtendedRenderStateDataKeys.TEXTURE_OVERRIDE.set(livingEntity, livingEntityRenderState);
    ExtendedRenderStateDataKeys.RENDER_HEAD.set(livingEntity, livingEntityRenderState);
  }
}
