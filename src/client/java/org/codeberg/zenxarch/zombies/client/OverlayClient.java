package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.ClientAssets.AssetInfo;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import org.codeberg.zenxarch.mob_variants_api.variant.OverlayAttachment;

public final class OverlayClient {
  private OverlayClient() {
    throw new IllegalStateException("Utility class");
  }

  public static OverlayAttachment make(EntityModelLayer layer, AssetInfo info) {
    return new OverlayAttachment(layer.id(), layer.name(), info);
  }

  public static EntityModelLayer getEntityModelLayer(OverlayAttachment overlay) {
    return new EntityModelLayer(overlay.layerId(), overlay.layerName());
  }
}
