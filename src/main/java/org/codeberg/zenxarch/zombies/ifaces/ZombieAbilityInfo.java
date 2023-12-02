package org.codeberg.zenxarch.zombies.ifaces;

public interface ZombieAbilityInfo {

  public void setFireImmunity(boolean fireImmune);
  public void setDaylightImmune(boolean daylightImmune);

  public boolean getFireImmune();
  public boolean getDaylightImmune();
}
