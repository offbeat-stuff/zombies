package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.codeberg.zenxarch.zombies.data.entity.MobAttachments;

public class ExtendedZombieEntityRenderer extends ZombieEntityRenderer {

  public ExtendedZombieEntityRenderer(Context context) {
    super(context);
    this.addFeature(new ExtendedZombieEntityOverlayRenderer(this));
  }

  @Override
  public ZombieEntityRenderState createRenderState() {
    return new ExtendedZombieEntityRenderState();
  }

  @Override
  public void updateRenderState(ZombieEntity zombie, ZombieEntityRenderState state, float f) {
    super.updateRenderState(zombie, state, f);
    if (state instanceof ExtendedZombieEntityRenderState exstate) {
      exstate.overlay = zombie.getAttached(MobAttachments.OVERLAY);
      exstate.renderHead = zombie.getAttachedOrElse(MobAttachments.RENDER_HEAD, true);
    }
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
