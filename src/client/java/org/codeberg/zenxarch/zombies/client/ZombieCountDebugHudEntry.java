package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.codeberg.zenxarch.zombies.spawning.SpawnerAttachments;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;
import org.codeberg.zenxarch.zombies.spawning.ZombieDensityMap;
import org.jetbrains.annotations.Nullable;

public class ZombieCountDebugHudEntry implements DebugHudEntry {

  @Override
  public void render(
      DebugHudLines lines,
      @Nullable World world,
      @Nullable WorldChunk clientChunk,
      @Nullable WorldChunk chunk) {

    if (world == null) return;
    if (!(world instanceof ServerWorld serverWorld)) return;

    var player = MinecraftClient.getInstance().player;
    if (player == null) return;
    var pos = player.getBlockPos();

    var zombies =
        SpawnerAttachments.getApocalypses(serverWorld).stream()
            .mapToInt(spawner -> getZombie(spawner, pos))
            .sum();
    lines.addLine("Extended Zombies : " + zombies);
  }

  private int getZombie(ZombieApocalypse apocalypse, BlockPos center) {
    var zombieCount = apocalypse.getZombieCount();
    if (zombieCount == null) return -1;
    return ZombieDensityMap.getSubMap(zombieCount, center).values().intStream().sum();
  }
}
