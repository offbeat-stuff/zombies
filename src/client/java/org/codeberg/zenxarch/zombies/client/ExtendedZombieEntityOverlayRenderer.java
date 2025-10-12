package org.codeberg.zenxarch.zombies.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.codeberg.zenxarch.mob_variants_api.variant.OverlayAttachment;

public class ExtendedZombieEntityOverlayRenderer
    extends FeatureRenderer<ZombieEntityRenderState, ZombieEntityModel<ZombieEntityRenderState>> {
  private static Map<EntityModelLayer, Optional<ZombieEntityModel<ZombieEntityRenderState>>>
      MODEL_CACHE = new HashMap<>();

  public ExtendedZombieEntityOverlayRenderer(
      FeatureRendererContext<ZombieEntityRenderState, ZombieEntityModel<ZombieEntityRenderState>>
          context) {
    super(context);
  }

  private Optional<ZombieEntityModel<ZombieEntityRenderState>> addModelToCache(
      OverlayAttachment overlay) {
    var layer = OverlayClient.getEntityModelLayer(overlay);
    if (MODEL_CACHE.containsKey(layer)) return MODEL_CACHE.get(layer);

    ZombieEntityModel<ZombieEntityRenderState> model = null;
    try {
      var loader = MinecraftClient.getInstance().getLoadedEntityModels();
      model = new ZombieEntityModel<>(loader.getModelPart(layer));
    } catch (Exception e) {
    }
    var result = Optional.ofNullable(model);
    MODEL_CACHE.put(layer, result);
    return result;
  }

  @Override
  public void render(
      MatrixStack matrices,
      OrderedRenderCommandQueue queue,
      int light,
      ZombieEntityRenderState state,
      float limbAngle,
      float limbDistance) {
    var overlay = ExtendedZombieEntityRenderer.OVERLAY.get(state);
    if (overlay.isEmpty()) return;
    var model = addModelToCache(overlay.get());
    if (model.isEmpty()) return;
    render(
        model.get(), overlay.get().texture().texturePath(), matrices, queue, light, state, -1, 1);
  }
}
