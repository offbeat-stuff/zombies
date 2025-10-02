package org.codeberg.zenxarch.zombies.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.codeberg.zenxarch.zombies.difficulty.DifficultyCalculations;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.jetbrains.annotations.Nullable;

public class ExtendedDifficultyDebugHudEntry implements DebugHudEntry {

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
    if (chunk == null || !world.isInHeightLimit(pos.getY())) return;
    var difficulty = new ExtendedDifficulty(serverWorld, pos);

    lines.addLine(
        String.format(
            "Extended Difficulty: %.2f // %.2f (Day %d,Hour %.1f)",
            difficulty.getLocalDifficulty(),
            difficulty.getClampedLocalDifficulty(),
            (int) DifficultyCalculations.getDays(serverWorld),
            DifficultyCalculations.getHoursInhabited(serverWorld, pos)));
  }
}
