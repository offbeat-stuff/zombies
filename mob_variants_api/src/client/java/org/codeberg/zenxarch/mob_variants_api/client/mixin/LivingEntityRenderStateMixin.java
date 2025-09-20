package org.codeberg.zenxarch.mob_variants_api.client.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.codeberg.zenxarch.mob_variants_api.client.ExtendedRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements ExtendedRenderState {
  @Unique private boolean zenxarch$hideHead;

  @Override
  public boolean zenxarch$getHideHead() {
    return zenxarch$hideHead;
  }

  @Override
  public void zenxarch$setHideHead(boolean value) {
    this.zenxarch$hideHead = value;
  }
}
