package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.class_12079.AssetInfo;
import org.codeberg.zenxarch.zombies.data.entity.OverlayAttachment;

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
