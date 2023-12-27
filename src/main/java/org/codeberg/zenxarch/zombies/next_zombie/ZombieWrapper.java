package org.codeberg.zenxarch.zombies.next_zombie;

import net.minecraft.entity.mob.ZombieEntity;
import org.codeberg.zenxarch.zombies.ifaces.ZombieAbilityInfo;

public class ZombieWrapper {

  private final ZombieEntity self;
  private boolean burnsUnderSun = true;
  private boolean burnsInGeneral = true;
  private boolean freezesInGeneral = true;

  public ZombieWrapper(ZombieEntity zombie) { this.self = zombie; }

  public ZombieEntity inner() { return self; }

  public ZombieEntity outer() {
    var f = (ZombieAbilityInfo)self;
    return self;
  }

  public ZombieWrapper doesNotBurnUnderSun() {
    this.burnsUnderSun = false;
    return this;
  }

  public ZombieWrapper doesNotBurnInGeneral() {
    this.burnsInGeneral = false;
    return this;
  }

  public ZombieWrapper doesNotFreezeInGeneral() {
    this.freezesInGeneral = false;
    return this;
  }
}
