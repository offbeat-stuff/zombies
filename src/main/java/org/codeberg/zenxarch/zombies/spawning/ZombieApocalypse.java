package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.spawner.SpecialSpawner;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.debug.Debug;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedZombieEntity;

public class ZombieApocalypse implements SpecialSpawner {
  private ServerWorld world;
  private final Debug debug = new Debug();
  private final SpawnProvider spawnProvider;

  public ZombieApocalypse(ServerWorld world) {
    this.world = world;
    this.spawnProvider = new SpawnProvider(world, debug);
  }

  private boolean canSpawnAtPosSpace(ZombieEntity zombie) {
    return this.world.doesNotIntersectEntities(zombie)
        && this.world.isSpaceEmpty(zombie)
        && !this.world.containsFluid(zombie.getBoundingBox());
  }

  public boolean spawnZombieAt(BlockPos ppos) {
    var difficulty = ExtendedDifficulty.getDifficulty(this.world, ppos);
    if (difficulty <= 0.0) return false;

    var zombieOpt =
        spawnProvider
            .giveSpawnPos(world, ppos, difficulty)
            .map(u -> new ExtendedZombieEntity(this.world, u))
            .filter(this::canSpawnAtPosSpace);

    zombieOpt.ifPresent(
        z -> {
          z.initialize(this.world);
          this.world.spawnEntityAndPassengers(z);
        });

    return zombieOpt.isPresent();
  }

  public boolean isSuitablePlayer(ServerPlayerEntity player) {
    return player.isAlive() && !player.isSpectator();
  }

  @Override
  public int spawn(ServerWorld world, boolean spawnMonsters, boolean spawnAnimals) {
    this.world = world;
    if (!spawnMonsters
        || this.world.getDifficulty().equals(Difficulty.PEACEFUL)
        || !this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
      return 0;
    }

    var result = 0;
    for (var player : this.world.getPlayers(this::isSuitablePlayer)) {
      result += this.spawnZombieAt(player.getBlockPos()) ? 1 : 0;
    }
    debug.attemptedSpawn(this.world, result > 0);
    return result;
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
