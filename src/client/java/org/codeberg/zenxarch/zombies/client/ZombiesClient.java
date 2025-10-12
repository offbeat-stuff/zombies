package org.codeberg.zenxarch.zombies.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.entity.EntityType;
import org.codeberg.zenxarch.zombies.client.debughud.ZombieDebugHudEntry;

public class ZombiesClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    EntityRendererFactories.register(EntityType.ZOMBIE, ExtendedZombieEntityRenderer::new);
    ZombieDebugHudEntry.initialize();
  }
}
