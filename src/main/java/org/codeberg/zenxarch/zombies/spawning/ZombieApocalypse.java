package org.codeberg.zenxarch.zombies.spawning;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.ZombieVariantRegistryHelper;
import org.codeberg.zenxarch.zombies.entity.variant.ZombieVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

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

  private void spawnZombie(ServerPlayerEntity player, BlockPos pos, ExtendedDifficulty difficulty) {
    ZombieVariantRegistryHelper.newZombie(world, pos).ifPresent(this::spawnZombie);
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

  public static final String ZOMBIE_ID_KEY = "zenxarch_zombie_id";

  private static Identifier toId(String id) {
    int i = id.indexOf(":");
    if (i >= 0) {
      String string = id.substring(i + 1);
      if (i != 0) {
        String string2 = id.substring(0, i);
        return Identifier.of(string2, string);
      }
      return Zombies.id(string);
    }
    return Zombies.id(id);
  }

  private static Optional<RegistryEntry<ZombieVariant>> fromId(World world, Identifier id) {
    return world
        .getRegistryManager()
        .getOrThrow(ZombieRegistryKeys.ZOMBIE_VARIANT)
        .getEntry(id)
        .map(Function.identity());
  }

  public static Optional<RegistryEntry<ZombieVariant>> getVariantFromNbt(
      World world, NbtCompound nbt) {
    var idKey = nbt.getString(ZOMBIE_ID_KEY);
    if (idKey.isEmpty()) return Optional.empty();
    return switch (idKey.get()) {
      case "" -> Optional.empty();
      case String id -> {
        try {
          yield fromId(world, toId(id));
        } catch (Exception e) {
          Zombies.LOGGER.info("Exception caught: {}", e.getMessage());
          yield Optional.empty();
        }
      }
    };
  }

  public static Optional<Entity> loadFromNbt(NbtCompound nbt, World world) {
    return getVariantFromNbt(world, nbt).map(variant -> loadFromVariant(world, variant, nbt));
  }

  private static ExtendedZombieEntity loadFromVariant(
      World world, RegistryEntry<ZombieVariant> variant, NbtCompound nbt) {
    var result = new ExtendedZombieEntity(world);
    result.readNbt(nbt);
    return result;
  }
}
