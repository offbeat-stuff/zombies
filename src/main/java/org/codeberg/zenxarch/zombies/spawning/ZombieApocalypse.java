package org.codeberg.zenxarch.zombies.spawning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.ZombieRegistry;

public class ZombieApocalypse {
  private ServerWorld world;
  private Map<BlockPos, Integer> zombieCount;

  public ZombieApocalypse(ServerWorld world) {
    this.world = world;
  }

  private void spawnZombie(ExtendedZombieEntity zombie) {
    zombie.setTarget(
        world.getClosestPlayer(
            zombie.getX(),
            zombie.getY(),
            zombie.getZ(),
            64.0,
            EntityPredicates.VALID_LIVING_ENTITY.and(
                EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)));
    zombie.initialize(this.world);
    this.world.spawnEntityAndPassengers(zombie);
  }

  private void spawnZombie(BlockPos pos) {
    ZombieRegistry.selectTemplate(world.getRandom(), world.getBiome(pos))
        .flatMap(t -> ZombieRegistry.newZombie(world, t, pos))
        .ifPresent(this::spawnZombie);
  }

  public void spawnZombiesAt(BlockPos playerPos, List<BlockPos> positions) {
    var difficulty = new ExtendedDifficulty(this.world, playerPos);
    var toSpawn = difficulty.getMaxZombies();
    if (toSpawn <= this.zombieCount.getOrDefault(playerPos, 0)) return;

    SpawnProvider.giveSpawnPositions(world, playerPos, positions, toSpawn)
        .ifPresent(this::spawnZombie);
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

  public static List<ServerPlayerEntity> players(ServerWorld world) {
    return world.getPlayers(
        EntityPredicates.VALID_LIVING_ENTITY.and(EntityPredicates.EXCEPT_SPECTATOR));
  }

  private Stream<BlockPos> spawnCenters() {
    return players(this.world).stream().map(ServerPlayerEntity::getBlockPos);
  }

  private void debugCheck(Map<BlockPos, Integer> zombieCount) {
    for (var player : players(this.world)) {
      var pos = player.getBlockPos();
      var difficulty = (int) (new ExtendedDifficulty(world, pos).getClampedLocalDifficulty() * 100);
      var zcount = zombieCount.getOrDefault(pos, 0);
      player.sendMessage(Text.of(difficulty + " : " + zcount), true);
    }
  }

  public void spawn(ServerWorld world, boolean spawnMonsters) {
    this.world = world;
    if (!spawnMonsters
        || this.world.getDifficulty().equals(Difficulty.PEACEFUL)
        || !this.world.getGameRules().getBoolean(ZombieGamerules.DO_ZOMBIE_SPAWNING)) {
      return;
    }

    var positions = spawnCenters().toList();
    this.zombieCount = countZombies(positions);

    if (Zombies.DEBUG) debugCheck(this.zombieCount);

    for (var v : positions) {
      spawnZombiesAt(v, positions);
    }
  }

  public static boolean isApocalypticWorld(ServerWorld world) {
    if (world.getRegistryKey().equals(World.OVERWORLD)) {
      return true;
    }
    return false;
  }

  public static final String ZOMBIE_ID_KEY = "zenxarch_zombie_id";

  public static Optional<Entity> loadFromNbt(NbtCompound nbt, World world) {
    return switch (nbt.getString(ZOMBIE_ID_KEY)) {
      case "" -> Optional.empty();
      case String id -> {
        var zombie = new ExtendedZombieEntity(world, ZombieRegistry.getTemplate(id));
        zombie.readNbt(nbt);
        yield Optional.of(zombie);
      }
    };
  }
}
