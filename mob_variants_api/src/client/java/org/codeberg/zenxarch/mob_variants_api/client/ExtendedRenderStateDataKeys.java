package org.codeberg.zenxarch.mob_variants_api.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;

public final class ExtendedRenderStateDataKeys {
  private ExtendedRenderStateDataKeys() {
    throw new IllegalStateException("Only static members");
  }

  public static final ExtendedRenderStateDataKey<Boolean> RENDER_HEAD =
      ExtendedRenderStateDataKey.<Boolean>builder("render_head")
          .attachment(MobAttachments.RENDER_HEAD)
          .build();

  public static final ExtendedRenderStateDataKey<Identifier> TEXTURE_OVERRIDE =
      ExtendedRenderStateDataKey.<Identifier>builder("texture_override")
          .attachment(MobAttachments.TEXTURE_OVERRIDE, assetInfo -> assetInfo.texturePath())
          .condition(ExtendedRenderStateDataKeys::isTextureLoaded)
          .build();

  private static boolean isTextureLoaded(Identifier texturePath) {
    return MinecraftClient.getInstance().getResourceManager().getResource(texturePath).isPresent();
  }
}
