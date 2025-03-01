package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.spawning.SpawnerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerChunkManager.class)
public abstract class ServerChunkManagerMixin {

  @Shadow private boolean spawnMonsters;

  @Shadow
  public abstract World getWorld();

  @Inject(
      method = "tickChunks(Lnet/minecraft/util/profiler/Profiler;JLjava/util/List;)V",
      at = @At("TAIL"))
  private void zenxarch$handle_spawners(CallbackInfo ci) {
    if (!(getWorld() instanceof ServerWorld sw)) return;
    if (!(getWorld() instanceof SpawnerProvider sp)) return;

    for (var spawner : sp.zenxarch$getZombieSpawners()) spawner.spawn(sw, spawnMonsters);
  }
}
