package org.codeberg.zenxarch.zombies.difficulty;

import static org.codeberg.zenxarch.zombies.Zombies.DEBUG_CONFIG;

import java.util.stream.Stream;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
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
    return false;
  }

  protected double getExtentedDifficulty() {
    return ExtendedDifficulty.getDifficulty(this.getWorld(), this.getBlockPos());
  }

  public void initialize(ServerWorldAccess world) {
    this.initialize(world, world.getLocalDifficulty(this.getBlockPos()), SpawnReason.NATURAL, null);
  }

  @Override
  public void tick() {
    super.tick();

    if (DEBUG_CONFIG.PARTICLES.value()
        && this.getWorld() instanceof ServerWorld serverWorld
        && this.isAlive()) {
      if (this.random.nextInt(20) == 0)
        this.runParticle(
            serverWorld, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5));
    }
  }

  private void runParticle(ServerWorld world, double x, double y, double z) {
    world.spawnParticles(ParticleTypes.LAVA, x, y, z, 2, 0, 0, 0, 0);
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
    if (difficulty <= 0.0) return;

    Stream.of(EquipmentSlot.values())
        .filter(slot -> this.getEquippedStack(slot).isEmpty())
        .forEach(
            slot ->
                Equipment.getEquipmentForSlot(random, difficulty, slot)
                    .ifPresent(item -> this.equipStack(slot, item.getDefaultStack())));
  }

  @Override
  protected void updateEnchantments(
      ServerWorldAccess world, Random random, LocalDifficulty unused) {
    var difficulty = getExtentedDifficulty();

    for (var slot : EquipmentSlot.values()) {
      if (this.getEquippedStack(slot).isEmpty()) return;

      this.equipStack(
          slot, Equipment.enchant(world, random, difficulty, this.getEquippedStack(slot)));
    }
  }

  @Override
  public boolean damage(DamageSource source, float amount) {
    if (!super.damage(source, amount)) return false;
    if (!(this.getWorld() instanceof ServerWorld serverWorld)) return false;

    return true;
  }

  @Override
  protected void applyAttributeModifiers(float chanceMultiplier) {
    super.applyAttributeModifiers(chanceMultiplier);
    var spawnAttribute = this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    spawnAttribute.setBaseValue(0.0);
    spawnAttribute.removeModifier(Identifier.ofVanilla("leader_zombie_bonus"));
  }

  @Override
  public void writeCustomDataToNbt(NbtCompound nbt) {
    super.writeCustomDataToNbt(nbt);
    nbt.putString(ZombieApocalypse.ZOMBIE_ID_KEY, ZombieApocalypse.BASE_ZOMBIE_ID);
  }
}
