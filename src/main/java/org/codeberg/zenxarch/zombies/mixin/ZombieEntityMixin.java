package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.entity.mob.ZombieEntity;
import org.codeberg.zenxarch.zombies.ifaces.ZombieAbilityInfo;
import org.codeberg.zenxarch.zombies.info.ZombieInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin implements ZombieAbilityInfo {

  @Unique private boolean zenxarch$zombies$isFireImmune = false;

  @Unique private boolean zenxarch$zombies$isDaylightImmune = false;

  @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
  public void
  zenxarch$zombies$modify_burnsInDaylight(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(!zenxarch$zombies$isDaylightImmune);
  }

  @Inject(method = "tickMovement", at = @At("HEAD"))
  public void zenxarch$zombies$modify_fireTicks(CallbackInfo ci) {
    var zombie = ((ZombieEntity)(Object)this);
    if (this.zenxarch$zombies$isFireImmune &&
        (zenxarch$zombies$isDaylightImmune ||
         !ZombieInfo.isAffectedByDaylight((ZombieEntity)(Object)this))) {
      zombie.extinguish();
    }
  }

  @Unique
  public void setFireImmunity(boolean fireImmune) {
    zenxarch$zombies$isFireImmune = fireImmune;
  }

  @Unique
  public boolean getFireImmune() {
    return zenxarch$zombies$isFireImmune;
  }

  @Unique
  public void setDaylightImmune(boolean daylightImmune) {
    zenxarch$zombies$isDaylightImmune = daylightImmune;
  }

  @Unique
  public boolean getDaylightImmune() {
    return zenxarch$zombies$isDaylightImmune;
  }
}
