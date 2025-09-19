package org.codeberg.zenxarch.mob_variants_api.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {

  @Unique private Identifier zenxarch$texture_override = null;

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
    if (this.zenxarch$texture_override != null
        && MinecraftClient.getInstance()
            .getResourceManager()
            .getResource(zenxarch$texture_override)
            .isPresent()) return this.zenxarch$texture_override;
    return op.call(self, renderState);
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
    if (livingEntity.hasAttached(MobAttachments.TEXTURE_OVERRIDE)) {
      this.zenxarch$texture_override =
          livingEntity.getAttached(MobAttachments.TEXTURE_OVERRIDE).texturePath();
    } else {
      this.zenxarch$texture_override = null;
    }
  }
}
