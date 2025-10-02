package org.codeberg.zenxarch.zombies.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.zombies.Zombies;

public class ZombiesClient implements ClientModInitializer {

  public static Identifier DEBUG_ZOMBIE_COUNT =
      DebugHudEntries.register(Zombies.id("zombie_count"), new ZombieCountDebugHudEntry());
  public static Identifier DEBUG_EXTENDED_DIFFICULTY =
      DebugHudEntries.register(
          Zombies.id("extended_difficulty"), new ExtendedDifficultyDebugHudEntry());

  @Override
  public void onInitializeClient() {
    EntityRendererFactories.register(EntityType.ZOMBIE, ExtendedZombieEntityRenderer::new);
  }
}
