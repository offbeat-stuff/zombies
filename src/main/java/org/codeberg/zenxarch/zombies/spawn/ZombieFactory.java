package org.codeberg.zenxarch.zombies.spawn;

import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.codeberg.zenxarch.zombies.info.WorldInfo;

public class ZombieFactory {

  public static Box createSimpleBoundingBox(EntityType<?> entityType,
                                            BlockPos pos) {
    return entityType.createSimpleBoundingBox(pos.getX() + 0.5, pos.getY(),
                                              pos.getZ() + 0.5);
  }

  public static void setZombiePos(ServerWorld world, ZombieEntity zombie,
                                  BlockPos pos) {
    zombie.refreshPositionAndAngles(
        (double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5,
        MathHelper.wrapDegrees(world.random.nextFloat() * 360.0F), 0.0F);

    zombie.headYaw = zombie.getYaw();
    zombie.bodyYaw = zombie.getYaw();
  }

  public interface ZombieEntry {

    public boolean posCheck(ServerWorld world, BlockPos pos);

    public Optional<ZombieEntity> create(ServerWorld world, BlockPos pos);

    default Optional<ZombieEntity> getSpawnedZombie(ServerWorld world,
                                                    BlockPos pos) {
      if (!posCheck(world, pos)) {
        return Optional.empty();
      }

      var zombieOpt = ZombieFactory.BASE_ZOMBIE.create(world, pos);
      if (zombieOpt.isEmpty()) {
        return zombieOpt;
      }
      var zombie = zombieOpt.get();

      world.spawnEntity(zombie);

      return Optional.of(zombie);
    }
  }

  public static ZombieEntry BASE_ZOMBIE = new ZombieEntry() {
    @Override
    public boolean posCheck(ServerWorld world, BlockPos pos) {
      if (!SpawnConditions.isWorldHostile(world) || !world.isNight() ||
          !WorldInfo.isOverworld(world)) {
        return false;
      }

      var simpleBoundingBox = createSimpleBoundingBox(EntityType.ZOMBIE, pos);

      if (!SpawnConditions.isBoxClear(world, simpleBoundingBox) ||
          !SpawnConditions.canSpawn(world, pos, EntityType.ZOMBIE,
                                    simpleBoundingBox, false)) {
        return false;
      }

      return true;
    }

    @Override
    public Optional<ZombieEntity> create(ServerWorld world, BlockPos pos) {
      var zombie = EntityType.ZOMBIE.create(world);

      if (zombie == null) {
        return Optional.empty();
      }

      setZombiePos(world, zombie, pos);

      if (!SpawnConditions.doesEntityFit(world, zombie)) {
        return Optional.empty();
      }

      zombie.initialize(world, world.getLocalDifficulty(pos),
                        SpawnReason.NATURAL, null, null);

      return Optional.of(zombie);
    }
  };
}