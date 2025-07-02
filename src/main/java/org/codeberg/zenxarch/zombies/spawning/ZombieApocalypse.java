package org.codeberg.zenxarch.zombies.spawning;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;

public class ZombieApocalypse {
  private ServerWorld world;
  private Object2IntMap<Vec3i> zombieCount;

  public ZombieApocalypse(ServerWorld world) {
    this.world = world;
  }

  private void spawnZombie(ExtendedZombieEntity zombie) {
    if (world.getGameRules().getBoolean(ZombieGamerules.ZOMBIE_TARGET_PLAYER_ON_SPAWN)) {
      zombie.setTarget(
          world.getClosestPlayer(
              zombie.getX(),
              zombie.getY(),
              zombie.getZ(),
              64.0,
              EntityPredicates.VALID_LIVING_ENTITY.and(
                  EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)));
    }
    zombie.initialize(this.world);
    this.world.spawnEntityAndPassengers(zombie);
  }

  private Optional<ExtendedZombieEntity> createZombie(BlockPos pos) {
    var zombie = new ExtendedZombieEntity(world);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    if (zombie.canSpawn(world)) return Optional.of(zombie);
    return Optional.empty();
  }

  private void spawnZombie(ServerPlayerEntity player, BlockPos pos, ExtendedDifficulty difficulty) {
    createZombie(pos).ifPresent(this::spawnZombie);
  }

  public void spawnZombiesNear(ServerPlayerEntity player, List<BlockPos> positions) {
    var difficulty = new ExtendedDifficulty(this.world, player.getBlockPos());
    var toSpawn = difficulty.getMaxZombies();

    var subMap = ZombieDensityMap.getSubMap(zombieCount, player.getBlockPos());
    var total = subMap.values().intStream().sum();
    if (toSpawn <= total) return;

    SpawnProvider.giveSpawnPositions(world, player.getBlockPos(), positions, subMap, toSpawn)
        .ifPresent(pos -> this.spawnZombie(player, pos, difficulty));
  }

  public Object2IntMap<Vec3i> countZombies() {
    var result = new Object2IntArrayMap<Vec3i>();
    for (var entity : world.iterateEntities()) {
      if (!(entity instanceof ExtendedZombieEntity zombie)) continue;
      ZombieDensityMap.push(result, zombie.getBlockPos());
    }
    return result;
  }

  public static List<ServerPlayerEntity> players(ServerWorld world) {
    return world.getPlayers(
        EntityPredicates.VALID_LIVING_ENTITY.and(EntityPredicates.EXCEPT_SPECTATOR));
  }

  private void debugCheck(Object2IntMap<Vec3i> zombieCount) {
    for (var player : players(this.world)) {
      var pos = player.getBlockPos();
      var difficulty = (int) (new ExtendedDifficulty(world, pos).getClampedLocalDifficulty() * 100);
      var zcount = ZombieDensityMap.getSubMap(zombieCount, pos).values().intStream().sum();
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

    var players = players(world);
    var positions = players.stream().map(ServerPlayerEntity::getBlockPos).toList();
    this.zombieCount = countZombies();

    if (FabricLoader.getInstance().isDevelopmentEnvironment()) debugCheck(this.zombieCount);

    for (var player : players) {
      spawnZombiesNear(player, positions);
    }
  }

  public static boolean isApocalypticWorld(ServerWorld world) {
    if (world.getRegistryKey().equals(World.OVERWORLD)) return true;
    return false;
  }
}
