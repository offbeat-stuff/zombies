package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class ExtendedZombieEntityRenderer extends ZombieEntityRenderer {

  public ExtendedZombieEntityRenderer(Context context) {
    super(context);
  }

  @Override
  protected void setupTransforms(
      ZombieEntityRenderState state, MatrixStack matrices, float bodyYaw, float baseHeight) {
    super.setupTransforms(state, matrices, bodyYaw, baseHeight);
    if (state.leaningPitch > 0f) {
      var finalPitch = MathHelper.lerp(state.leaningPitch, 0f, -10f - state.pitch);
      matrices.multiply(
          RotationAxis.POSITIVE_X.rotationDegrees(finalPitch),
          0,
          (state.height / 2) / baseHeight,
          0);
    }
  }
}
