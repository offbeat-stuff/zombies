package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty.IExtendedDifficulty;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;

public class ExtendedZombieEntity extends ZombieEntity {

  public ExtendedZombieEntity(World world) { super(world); }

  public ExtendedZombieEntity(World world, BlockPos pos) {
    super(world);
    this.refreshPositionAndAngles(pos, this.random.nextFloat() * 360.0F, 0.0F);
  }

  @Override
  protected boolean burnsInDaylight() {
    return false;
  }

  protected IExtendedDifficulty getExtentedDifficulty() {
    return ExtendedDifficulty.difficulty(this.getWorld(), this.getBlockPos());
  }

  public void initialize(ServerWorldAccess world) {
    this.initialize(world, world.getLocalDifficulty(this.getBlockPos()),
                    SpawnReason.NATURAL, null, null);
  }

  @Override
  public EntityData initialize(ServerWorldAccess world,
                               LocalDifficulty difficulty,
                               SpawnReason spawnReason, EntityData entityData,
                               NbtCompound entityNbt) {
    entityData = new ZombieData(false, false);
    return super.initialize(world, difficulty, spawnReason, entityData,
                            entityNbt);
  }

  @Override
  protected void initEquipment(Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty();

    for (var slot : EquipmentSlot.values()) {
      if (!this.getEquippedStack(slot).isEmpty()) {
        continue;
      }
      var item = difficulty.getEquipmentForSlot(slot);
      if (item.isEmpty()) {
        continue;
      }
      this.equipStack(slot, item.get().getDefaultStack());
    }
  }

  @Override
  protected void updateEnchantments(Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty();

    for (var slot : EquipmentSlot.values()) {
      if (this.getEquippedStack(slot).isEmpty()) {
        return;
      }

      this.equipStack(slot, difficulty.enchant(this.getEquippedStack(slot)));
    }
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putString(ZombieApocalypse.ZOMBIE_ID_KEY,
                  ZombieApocalypse.BASE_ZOMBIE_ID);
  }
}
