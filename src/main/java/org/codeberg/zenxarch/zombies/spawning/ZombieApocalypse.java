package org.codeberg.zenxarch.zombies.spawning;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.spawner.SpecialSpawner;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.debug.Debug;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedZombieEntity;

public class ZombieApocalypse implements SpecialSpawner {

  private final Random random = Random.create();
  private ServerWorld world;
  private Debug debug = new Debug();

  public ZombieApocalypse(ServerWorld world) { this.world = world; }

  private boolean canSpawnAtPosBasic(BlockPos pos) {
    if (world.getLightLevel(LightType.BLOCK, pos) > 0) {
      return false;
    }

    if (this.world.isPlayerInRange(pos.getX(), pos.getY(), pos.getZ(), 16)) {
      return false;
    }

    var entityType = EntityType.ZOMBIE;
    var location = SpawnRestriction.getLocation(entityType);

    if (!SpawnHelper.canSpawn(location, this.world, pos, entityType) ||
        !MobEntity.canMobSpawn(EntityType.ZOMBIE, this.world,
                               SpawnReason.NATURAL, pos, this.world.random)) {
      return false;
    };

    debug.spawnCheck();

    var boundingBox = EntityType.ZOMBIE.createSimpleBoundingBox(
        pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

    return this.world.doesNotIntersectEntities(
               null, VoxelShapes.cuboid(boundingBox)) &&
        this.world.isSpaceEmpty(boundingBox) &&
        !this.world.containsFluid(boundingBox);
  }

  private boolean canSpawnAtPosSpace(ZombieEntity zombie) {
    return this.world.doesNotIntersectEntities(zombie) &&
        this.world.isSpaceEmpty(zombie) &&
        !this.world.containsFluid(zombie.getBoundingBox());
  }

  private static int SPAWN_RANGE = 64;

  private Optional<BlockPos> findNearestWorking(BlockPos pos, int times) {
    for (int i = 0; i < times; i++) {
      var p = pos.add(random.nextBetween(-SPAWN_RANGE, SPAWN_RANGE),
                      random.nextBetween(-SPAWN_RANGE, SPAWN_RANGE),
                      random.nextBetween(-SPAWN_RANGE, SPAWN_RANGE));
      if (canSpawnAtPosBasic(p)) {
        return Optional.of(p);
      }
    }
    return Optional.empty();
  }

  public boolean spawnZombieAt(BlockPos ppos) {
    var difficulty = new ExtendedDifficulty(this.world, ppos);
    var zombieOpt = findNearestWorking(ppos, difficulty.getTriesForSpawning())
                        .map(u -> new ExtendedZombieEntity(this.world, u))
                        .filter(this::canSpawnAtPosSpace);

    zombieOpt.ifPresent(z -> {
      z.initialize(this.world);
      this.world.spawnEntityAndPassengers(z);
    });

    return zombieOpt.isPresent();
  }

  public boolean isSuitablePlayer(ServerPlayerEntity player) {
    return player.isAlive() && !player.isSpectator();
  }

  @Override
  public int spawn(ServerWorld world, boolean spawnMonsters,
                   boolean spawnAnimals) {
    this.world = world;
    if (!spawnMonsters) {
      return 0;
    }

    if (this.world.getDifficulty().equals(Difficulty.PEACEFUL) ||
        !this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
      return 0;
    }

    var result = 0;
    for (var player : this.world.getPlayers(this::isSuitablePlayer)) {
      result += this.spawnZombieAt(player.getBlockPos()) ? 1 : 0;
    }
    debug.attemptedSpawn(result > 0);
    return result;
  }

  public static boolean isApocalypticWorld(ServerWorld world) {
    if (world.getRegistryKey().equals(World.OVERWORLD)) {
      return true;
    }
    return false;
  }

  public static String ZOMBIE_ID_KEY = "zenxarch_zombie_id";
  public static String BASE_ZOMBIE_ID = "BaseZombie";

  public static Optional<Entity> loadFromNbt(NbtCompound nbt, World world) {
    var id = nbt.getString(ZOMBIE_ID_KEY);
    if (id.equals("")) {
      return Optional.empty();
    }
    if (id.equals(BASE_ZOMBIE_ID)) {
      var zombie = new ExtendedZombieEntity(world);
      zombie.readNbt(nbt);
      return Optional.of(zombie);
    }
    Zombies.LOGGER.warn("Skipping Zombie Apocalypse Entity with id {}", id);
    return Optional.empty();
  }
}
