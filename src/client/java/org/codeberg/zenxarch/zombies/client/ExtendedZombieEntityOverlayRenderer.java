package org.codeberg.zenxarch.zombies.client;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.mob_variants_api.variant.OverlayAttachment;

public class ExtendedZombieEntityOverlayRenderer
    extends FeatureRenderer<ZombieEntityRenderState, ZombieEntityModel<ZombieEntityRenderState>> {
  private Identifier texture;
  private ZombieEntityModel<ZombieEntityRenderState> model;
  private OverlayAttachment overlay;

  public ExtendedZombieEntityOverlayRenderer(
      FeatureRendererContext<ZombieEntityRenderState, ZombieEntityModel<ZombieEntityRenderState>>
          context) {
    super(context);
    this.overlay = null;
  }

  private void setOverlay(OverlayAttachment overlay) {
    if (Objects.equals(this.overlay, overlay)) return;
    this.overlay = overlay;
    if (this.overlay == null) return;
    this.texture = this.overlay.texture().texturePath();
    try {
      var loader = MinecraftClient.getInstance().getLoadedEntityModels();
      this.model =
          new ZombieEntityModel<>(
              loader.getModelPart(OverlayClient.getEntityModelLayer(this.overlay)));
    } catch (Exception e) {
      this.model = null;
    }
  }

  @Override
  public void render(
      MatrixStack matrices,
      OrderedRenderCommandQueue queue,
      int light,
      ZombieEntityRenderState state,
      float limbAngle,
      float limbDistance) {
    ExtendedZombieEntityRenderer.OVERLAY.get(state).ifPresent(this::setOverlay);
    if (this.overlay == null) return;
    if (this.model == null) return;
    render(this.model, this.texture, matrices, queue, light, state, -1, 1);
  }
}
