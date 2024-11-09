package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;

public class ExtendedZombieEntity extends ZombieEntity {
  public ExtendedZombieEntity(World world) {
    super(world);
  }

  public ExtendedZombieEntity(World world, BlockPos pos) {
    super(world);
    this.refreshPositionAndAngles(pos, this.random.nextFloat() * 360.0F, 0.0F);
  }

  @Override
  protected boolean burnsInDaylight() {
    if (this.getWorld() instanceof ServerWorld sw)
      return sw.getGameRules().getBoolean(ZombieGamerules.ZOMBIES_BURN_IN_DAYLIGHT);
    return false;
  }

  protected ExtendedDifficulty getExtentedDifficulty() {
    if (this.getWorld() instanceof ServerWorld serverWorld)
      return new ExtendedDifficulty(serverWorld, this.getBlockPos());
    return null;
  }

  public void initialize(ServerWorldAccess world) {
    this.initialize(world, world.getLocalDifficulty(this.getBlockPos()), SpawnReason.NATURAL, null);
  }

  @Override
  public EntityData initialize(
      ServerWorldAccess world,
      LocalDifficulty difficulty,
      SpawnReason spawnReason,
      EntityData entityData) {
    entityData = new ZombieData(false, false);
    return super.initialize(world, difficulty, spawnReason, entityData);
  }

  @Override
  protected void initEquipment(Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty();
    if (!difficulty.isHarderThan(0.0f)) return;

    for (var slot : EquipmentSlot.values()) {
      if (!this.getEquippedStack(slot).isEmpty()) continue;

      var equipment = Equipment.getEquipmentForSlot(random, difficulty, slot);
      equipment
          .map(Item::getDefaultStack)
          .ifPresent(
              s -> {
                this.equipStack(slot, s);
                this.setEquipmentDropChance(slot, 0.00075F);
              });
    }
  }

  @Override
  protected void updateEnchantments(
      ServerWorldAccess world, Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty();

    for (var slot : EquipmentSlot.values()) {
      var stack = this.getEquippedStack(slot);
      if (stack.isEmpty()) continue;

      stack = Equipment.enchant(world, random, difficulty, stack);
      this.equipStack(slot, stack);
    }
  }

  @Override
  public boolean damage(ServerWorld world, DamageSource source, float amount) {
    if (!super.damage(world, source, amount)) return false;
    if (!(world instanceof SpawnerProvider spawnerProvider)) return false;
    for (var spawner : spawnerProvider.getSpawners()) spawner.spawn(world, true);
    return true;
  }

  @Override
  protected void applyAttributeModifiers(float chanceMultiplier) {
    super.applyAttributeModifiers(chanceMultiplier);
    var spawnAttribute = this.getAttributeInstance(EntityAttributes.SPAWN_REINFORCEMENTS);
    spawnAttribute.setBaseValue(0.0);
    spawnAttribute.removeModifier(Identifier.ofVanilla("leader_zombie_bonus"));
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putString(ZombieApocalypse.ZOMBIE_ID_KEY, ZombieApocalypse.BASE_ZOMBIE_ID);
  }
}
