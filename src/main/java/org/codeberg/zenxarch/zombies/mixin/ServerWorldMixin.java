package org.codeberg.zenxarch.zombies.mixin;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.SpecialSpawner;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {

  @Unique private List<SpecialSpawner> zombieSpawners;

  @Inject(at = @At(value = "TAIL"), method = "<init>", cancellable = false)
  private void zenxarch$inject_init(CallbackInfo ci) {
    var world = (ServerWorld)(Object)this;
    zombieSpawners = ZombieApocalypse.isApocalypticWorld(world)
                         ? ImmutableList.of(new ZombieApocalypse(world))
                         : ImmutableList.of();
  }

  @Inject(at = @At(value = "HEAD"), method = "tickSpawners")
  private void zenxarch$inject_tickSpawners(boolean spawnMonsters,
                                            boolean spawnAnimals,
                                            CallbackInfo ci) {
    for (var spawner : zombieSpawners) {
      spawner.spawn((ServerWorld)(Object)this, spawnMonsters, spawnAnimals);
    }
  }
}
