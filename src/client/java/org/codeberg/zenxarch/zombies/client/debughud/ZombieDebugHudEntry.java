package org.codeberg.zenxarch.zombies.client.debughud;

import net.minecraft.client.gui.hud.debug.DebugHudEntries;
import net.minecraft.client.gui.hud.debug.DebugHudEntry;
import net.minecraft.client.gui.hud.debug.DebugHudLines;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.zombies.Zombies;

public interface ZombieDebugHudEntry extends DebugHudEntry {
  public static Identifier ZOMBIE_SECTION = Zombies.id("zombie_debug_section");

  public static Identifier DEBUG_ZOMBIE_COUNT =
      DebugHudEntries.register(Zombies.id("zombie_count"), new ZombieCountDebugHudEntry());
  public static Identifier DEBUG_EXTENDED_DIFFICULTY =
      DebugHudEntries.register(
          Zombies.id("extended_difficulty"), new ExtendedDifficultyDebugHudEntry());
  public static Identifier DEBUG_ZOMBIE_KILLS =
      DebugHudEntries.register(Zombies.id("zombie_kills"), new ZombieKillCountDebugHudEntry());

  default void addLine(DebugHudLines lines, String line) {
    lines.addLineToSection(ZOMBIE_SECTION, line);
  }

  public static void initialize() {
    // force load class
  }
}
