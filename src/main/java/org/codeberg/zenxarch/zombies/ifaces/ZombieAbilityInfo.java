package org.codeberg.zenxarch.zombies.ifaces;

public interface ZombieAbilityInfo {

  public void setBurnsInGeneral(boolean burnsInGeneral);
  public void setBurnsUnderSun(boolean burnsUnderSun);

  public boolean getBurnsInGeneral();
  public boolean getBurnsUnderSun();
}
