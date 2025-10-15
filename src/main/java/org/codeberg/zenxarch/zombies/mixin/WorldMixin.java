package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(World.class)
public abstract class WorldMixin {
  @Unique
  public long method_8510() {
    return ((World) (Object) this).getTime();
  }
}
