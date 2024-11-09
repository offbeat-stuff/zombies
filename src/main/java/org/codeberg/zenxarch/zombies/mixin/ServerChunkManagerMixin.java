package org.codeberg.zenxarch.zombies.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.profiler.Profiler;
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerChunkManager.class)
public class ServerChunkManagerMixin {
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

    var spawnMonsters = false;

    try {
      var smField = ServerChunkManager.class.getDeclaredField("spawnMonsters");
      smField.setAccessible(true);
      spawnMonsters = smField.getBoolean(chunkManager);
      smField.setAccessible(false);
    } catch (Exception e) {
      return;
    }

    for (var spawner : sp.getSpawners()) {
      spawner.spawn(sw, spawnMonsters);
    }
  }
}
