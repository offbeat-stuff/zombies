package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.server.MinecraftServer;
import org.codeberg.zenxarch.zombies.spawn.ZombieSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
  @Inject(at = @At("TAIL"), method = "tickWorlds")
  private void zenxarch$zombies$tick_end(CallbackInfo info) {
    ZombieSpawner.spawnZombiesForEachWorld((MinecraftServer)(Object)this);
  }
}