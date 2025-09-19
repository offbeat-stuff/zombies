package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import org.codeberg.zenxarch.zombies.data.entity.OverlayAttachment;

public class ExtendedZombieEntityRenderState extends ZombieEntityRenderState {
  public OverlayAttachment overlay;
  public boolean renderHead;
}
