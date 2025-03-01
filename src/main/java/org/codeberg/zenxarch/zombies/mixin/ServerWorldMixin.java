package org.codeberg.zenxarch.zombies.mixin;

import java.util.List;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements SpawnerProvider {
  @Unique private List<ZombieApocalypse> zenxarch$zombieSpawners;

  @Inject(at = @At(value = "TAIL"), method = "<init>", cancellable = false)
  private void zenxarch$inject_init(CallbackInfo ci) {
    var world = (ServerWorld) (Object) this;
    zenxarch$zombieSpawners =
        ZombieApocalypse.isApocalypticWorld(world)
            ? List.of(new ZombieApocalypse(world))
            : List.of();
  }

  @Unique
  @Override
  public List<ZombieApocalypse> zenxarch$getZombieSpawners() {
    return zenxarch$zombieSpawners;
  }
}
