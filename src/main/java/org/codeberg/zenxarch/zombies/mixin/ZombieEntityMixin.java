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

  @Unique private boolean zenxarch$zombies$burnsInGeneral = true;

  @Unique private boolean zenxarch$zombies$burnsUnderSun = true;

  @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
  public void
  zenxarch$zombies$modify_burnsInDaylight(CallbackInfoReturnable<Boolean> cir) {
    cir.setReturnValue(zenxarch$zombies$burnsUnderSun);
  }

  @Inject(method = "tickMovement", at = @At("HEAD"))
  public void zenxarch$zombies$modify_fireTicks(CallbackInfo ci) {
    var zombie = ((ZombieEntity)(Object)this);
    if (!this.zenxarch$zombies$burnsUnderSun &&
        ZombieInfo.isAffectedByDaylight(zombie)) {
      zombie.extinguish();
    }

    if (!this.zenxarch$zombies$burnsInGeneral) {
      zombie.extinguish();
    }
  }

  @Override
  public void setBurnsInGeneral(boolean burnsInGeneral) {
    this.zenxarch$zombies$burnsInGeneral = burnsInGeneral;
  }

  @Override
  public void setBurnsUnderSun(boolean burnsUnderSun) {
    this.zenxarch$zombies$burnsUnderSun = burnsUnderSun;
  }

  @Override
  public boolean getBurnsInGeneral() {
    return this.zenxarch$zombies$burnsInGeneral;
  }

  @Override
  public boolean getBurnsUnderSun() {
    return this.zenxarch$zombies$burnsUnderSun;
  }
}
