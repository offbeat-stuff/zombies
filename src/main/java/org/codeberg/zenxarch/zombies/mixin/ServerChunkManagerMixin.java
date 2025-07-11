package org.codeberg.zenxarch.zombies.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {

  @Shadow private boolean spawnMonsters;

  @Shadow
  public abstract World getWorld();

  @SuppressWarnings("resource")
  @WrapOperation(
      method = "tickChunks",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V"))
  private void zenxarch$handle_spawners(Profiler profiler, String location, Operation<Void> op) {
    op.call(profiler, location);
    if (location != "customSpawners") return;
    var chunkManager = ((ServerChunkManager) (Object) this);
    var world = chunkManager.getWorld();
    if (!(world instanceof ServerWorld sw)) return;
    if (!(world instanceof SpawnerProvider sp)) return;

    for (var spawner : sp.zenxarch$getZombieSpawners()) {
      spawner.spawn(sw, this.spawnMonsters);
    }
  }
}
