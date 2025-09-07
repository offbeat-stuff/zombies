package org.codeberg.zenxarch.zombies.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;

public class ZombiesClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    EntityRendererRegistry.register(EntityType.ZOMBIE, ExtendedZombieEntityRenderer::new);
  }
}
