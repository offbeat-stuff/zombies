package org.codeberg.zenxarch.mob_variants_api.client;

import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public final class ExtendedRenderStateKeys {
  private ExtendedRenderStateKeys() {
    throw new IllegalStateException("Only static members");
  }

  public static final RenderStateDataKey<Boolean> RenderHead =
      RenderStateDataKey.create(() -> "zenxarch:render_head");
  public static final RenderStateDataKey<Identifier> TextureOverride =
      RenderStateDataKey.create(() -> "zenxarch:texture_override");

  public static boolean getRenderHead(FabricRenderState state) {
    var value = state.getData(RenderHead);
    return value == null ? true : value;
  }

  public static Identifier getTextureOverride(FabricRenderState state) {
    var value = state.getData(TextureOverride);
    if (value == null) return null;
    if (MinecraftClient.getInstance().getResourceManager().getResource(value).isPresent())
      return value;
    return null;
  }
}
