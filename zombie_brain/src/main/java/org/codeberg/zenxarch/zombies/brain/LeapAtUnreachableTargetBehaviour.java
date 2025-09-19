package org.codeberg.zenxarch.zombies.brain;

import net.minecraft.entity.mob.MobEntity;

// import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.LeapAtTarget;
// import net.tslat.smartbrainlib.object.MemoryTest;
// import net.tslat.smartbrainlib.registry.SBLMemoryTypes;
// import net.tslat.smartbrainlib.util.BrainUtil;

public class LeapAtUnreachableTargetBehaviour<E extends MobEntity> {
  // extends LeapAtTarget<E> {

  // private static final MemoryTest MEMORY_REQUIREMENTS =
  //     MemoryTest.builder(4)
  //         .hasMemories(
  //             MemoryModuleType.ATTACK_TARGET,
  //             MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
  //             SBLMemoryTypes.TARGET_UNREACHABLE.get())
  //         .noMemory(MemoryModuleType.ATTACK_COOLING_DOWN);

  // @Override
  // protected List<Pair<MemoryModuleType<?>, MemoryModuleState>> getMemoryRequirements() {
  //   return MEMORY_REQUIREMENTS;
  // }

  // public LeapAtUnreachableTargetBehaviour(int delayTicks) {
  //   super(delayTicks);
  // }

  // @Override
  // protected boolean checkExtraStartConditions(ServerWorld level, E entity) {
  //   return super.checkExtraStartConditions(level, entity)
  //       && BrainUtil.getMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE) > 100;
  // }
}
