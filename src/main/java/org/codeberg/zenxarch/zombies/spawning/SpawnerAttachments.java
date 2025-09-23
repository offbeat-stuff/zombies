package org.codeberg.zenxarch.zombies.spawning;

import java.util.List;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.ZModUtils;

public final class SpawnerAttachments {
  private SpawnerAttachments() {
    throw new IllegalStateException("Utility class");
  }

  public static final AttachmentType<List<ZombieApocalypse>> ZOMBIE_APOCALYPSE =
      AttachmentRegistry.create(ZModUtils.id("zombie_apocalypse"));

  public static List<ZombieApocalypse> getApocalypses(ServerWorld world) {
    return world.getAttachedOrSet(
        ZOMBIE_APOCALYPSE,
        ZombieApocalypse.isApocalypticWorld(world)
            ? List.of(new ZombieApocalypse(world))
            : List.of());
  }

  public static void initialize() {
    // force load class
  }
}
