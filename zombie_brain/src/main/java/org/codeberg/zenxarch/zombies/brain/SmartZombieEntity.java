package org.codeberg.zenxarch.zombies.brain;

import java.util.List;
import net.minecraft.entity.ai.brain.Brain.Profile;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

public abstract class SmartZombieEntity extends ZombieEntity
    implements SmartBrainOwner<SmartZombieEntity> {

  private static ZombieBrain<SmartZombieEntity> BRAIN_PROVIDER = new ZombieBrain<>();

  public SmartZombieEntity(World world) {
    super(world);
  }

  @Override
  protected Profile<?> createBrainProfile() {
    return BRAIN_PROVIDER.getBrainProfile(this);
  }

  @Override
  public List<ExtendedSensor<SmartZombieEntity>> getSensors() {
    return BRAIN_PROVIDER.getSensors();
  }

  @Override
  public BrainActivityGroup<? extends SmartZombieEntity> getCoreTasks() {
    return BRAIN_PROVIDER.getCoreTasks(SmartZombieEntity::burnsInDaylight);
  }

  @Override
  public BrainActivityGroup<? extends SmartZombieEntity> getIdleTasks() {
    return BRAIN_PROVIDER.getIdleTasks();
  }

  @Override
  public BrainActivityGroup<? extends SmartZombieEntity> getFightTasks() {
    return BRAIN_PROVIDER.getFightTasks();
  }

  @Override
  protected void initGoals() {
    /* use smartbrainlib for ai */
  }

  @Override
  public void setCanBreakDoors(boolean canBreakDoors) {
    /* no griefing */
  }

  @Override
  protected EntityNavigation createNavigation(World world) {
    return BRAIN_PROVIDER.createNavigation(this, world);
  }

  @Override
  protected void mobTick(ServerWorld world) {
    super.mobTick(world);
    tickBrain(this);
  }
}
