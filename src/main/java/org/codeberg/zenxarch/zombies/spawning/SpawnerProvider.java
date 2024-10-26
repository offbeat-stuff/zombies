package org.codeberg.zenxarch.zombies.spawning;

import java.util.List;
import net.minecraft.world.spawner.SpecialSpawner;

public interface SpawnerProvider {
  public List<SpecialSpawner> getSpawners();
}
