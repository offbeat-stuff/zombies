package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.codeberg.zenxarch.zombies.ZombieHealth;

public class ZombieKillCountDebugHudEntry implements DebugHudEntry {

  @Override
  public void render(DebugHudLines lines, World world, WorldChunk clientChunk, WorldChunk chunk) {
    var player = MinecraftClient.getInstance().player;
    if (player == null) return;
    lines.addLine(
        String.format("Zombie kills : %d", player.getAttachedOrCreate(ZombieHealth.ZOMBIE_KILLS)));
  }
}
