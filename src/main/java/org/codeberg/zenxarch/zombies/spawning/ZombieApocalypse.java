package org.codeberg.zenxarch.zombies.spawning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.spawner.SpecialSpawner;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedZombieEntity;

public class ZombieApocalypse implements SpecialSpawner {
  private ServerWorld world;

  public ZombieApocalypse(ServerWorld world) {
    this.world = world;
  }

  private boolean canSpawnAtPosSpace(ZombieEntity zombie) {
    return this.world.doesNotIntersectEntities(zombie)
        && this.world.isSpaceEmpty(zombie)
        && !this.world.containsFluid(zombie.getBoundingBox());
  }

  private int spawnZombie(ExtendedZombieEntity zombie) {
    zombie.initialize(this.world);
    this.world.spawnEntityAndPassengers(zombie);
    return 1;
  }

  public int spawnZombiesAt(BlockPos playerPos) {
    var difficulty = ExtendedDifficulty.getDifficulty(this.world, playerPos);
    if (difficulty <= 0.0) return 0;

    return SpawnProvider.giveSpawnPositions(world, playerPos, difficulty)
        .map(u -> new ExtendedZombieEntity(this.world, u))
        .filter(this::canSpawnAtPosSpace)
        .map(this::spawnZombie)
        .orElse(0);
  }

  public Map<BlockPos, Integer> countZombies(List<BlockPos> positions) {
    var result = new HashMap<BlockPos, Integer>();
    for (var pos : positions) result.put(pos, 0);
    for (var entity : world.iterateEntities()) {
      if (!(entity instanceof ExtendedZombieEntity zombie)) continue;
      for (var pos : positions)
        if (zombie.getBlockPos().isWithinDistance(pos, 128)) result.put(pos, result.get(pos) + 1);
    }
    return result;
  }

  private List<ServerPlayerEntity> players() {
    return this.world.getPlayers(
        EntityPredicates.VALID_LIVING_ENTITY.and(EntityPredicates.EXCEPT_SPECTATOR));
  }

  private Stream<BlockPos> spawnCenters() {
    return players().stream().map(ServerPlayerEntity::getBlockPos);
  }

  @Override
  public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
    this.world = world;
    if (!spawnMonsters
        || this.world.getDifficulty().equals(Difficulty.PEACEFUL)
        || !this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
      return 0;
    }

    var positions = spawnCenters().toList();
    var zombieCount = countZombies(positions);

    return positions.stream()
        .filter(
            pos ->
                zombieCount.get(pos)
                    < ExtendedDifficulty.getMaxZombies(
                        ExtendedDifficulty.getDifficulty(world, pos)))
        .mapToInt(this::spawnZombiesAt)
        .sum();
  }

  public static boolean isApocalypticWorld(ServerWorld world) {
    if (world.getRegistryKey().equals(World.OVERWORLD)) {
      return true;
    }
    return false;
  }

  public static final String ZOMBIE_ID_KEY = "zenxarch_zombie_id";
  public static final String BASE_ZOMBIE_ID = "BaseZombie";

  public static Optional<Entity> loadFromNbt(NbtCompound nbt, World world) {
    return switch (nbt.getString(ZOMBIE_ID_KEY)) {
      case "" -> Optional.empty();
      case BASE_ZOMBIE_ID -> {
        var zombie = new ExtendedZombieEntity(world);
        zombie.readNbt(nbt);
        yield Optional.of(zombie);
      }
      case String id -> {
        Zombies.LOGGER.warn("Skipping Zombie Apocalypse Entity with id {}", id);
        yield Optional.empty();
      }
    };
  }
}
