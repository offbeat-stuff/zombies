package org.codeberg.zenxarch.zombies.client.debughud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.codeberg.zenxarch.zombies.ZombieHealth;

public class ZombieKillCountDebugHudEntry implements ZombieDebugHudEntry {

  @Override
  public void render(DebugHudLines lines, World world, WorldChunk clientChunk, WorldChunk chunk) {
    var player = MinecraftClient.getInstance().player;
    if (player == null) return;
    addLine(
        lines,
        String.format("Zombie kills : %d", player.getAttachedOrCreate(ZombieHealth.ZOMBIE_KILLS)));
  }
}
