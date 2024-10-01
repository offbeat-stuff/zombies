package org.codeberg.zenxarch.zombies.mixin;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.codeberg.zenxarch.zombies.spawning.ZombieApocalypse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin {
  @Inject(method = "getEntityFromNbt", at = @At("HEAD"), cancellable = true)
  private static void zenxarch$inject_getEntityFromNbt(
      NbtCompound nbt, World world, CallbackInfoReturnable<Optional<Entity>> cir) {
    var opt = ZombieApocalypse.loadFromNbt(nbt, world);
    if (opt.isPresent()) {
      cir.setReturnValue(opt);
    }
  }
}
