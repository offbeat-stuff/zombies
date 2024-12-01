package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import org.codeberg.zenxarch.zombies.entity.effect.DefaultZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;

public record ZombieEvents(
    ZombieEffect spawn,
    ZombieEffect tick,
    ZombieEffect attack,
    ZombieEffect damage,
    ZombieEffect death,
    ZombieEffect kill,
    ZombieEffect killed) {

  public static final ZombieEvents DEFAULT = new ZombieEvents();

  private ZombieEvents() {
    this(
        DefaultZombieEffect.create(),
        DefaultZombieEffect.create(),
        DefaultZombieEffect.create(),
        DefaultZombieEffect.create(),
        DefaultZombieEffect.create(),
        DefaultZombieEffect.create(),
        DefaultZombieEffect.create());
  }

  private static final RecordCodecBuilder<ZombieEvents, ZombieEffect> fieldOf(
      String name, Function<ZombieEvents, ZombieEffect> getter) {
    return ZombieEffect.CODEC.optionalFieldOf(name, DefaultZombieEffect.create()).forGetter(getter);
  }

  public static final Codec<ZombieEvents> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      fieldOf("spawn", ZombieEvents::spawn),
                      fieldOf("tick", ZombieEvents::tick),
                      fieldOf("attack", ZombieEvents::attack),
                      fieldOf("damage", ZombieEvents::damage),
                      fieldOf("death", ZombieEvents::death),
                      fieldOf("kill", ZombieEvents::kill),
                      fieldOf("killed", ZombieEvents::killed))
                  .apply(instance, ZombieEvents::new));

  public static class Builder {
    ZombieEffect spawn = DefaultZombieEffect.create();
    ZombieEffect tick = DefaultZombieEffect.create();
    ZombieEffect attack = DefaultZombieEffect.create();
    ZombieEffect damage = DefaultZombieEffect.create();
    ZombieEffect death = DefaultZombieEffect.create();
    ZombieEffect kill = DefaultZombieEffect.create();
    ZombieEffect killed = DefaultZombieEffect.create();

    public Builder onSpawn(ZombieEffect effect) {
      spawn = effect;
      return this;
    }

    public Builder onTick(ZombieEffect effect) {
      tick = effect;
      return this;
    }

    public Builder onAttack(ZombieEffect effect) {
      attack = effect;
      return this;
    }

    public Builder onDamage(ZombieEffect effect) {
      damage = effect;
      return this;
    }

    public Builder onDeath(ZombieEffect effect) {
      death = effect;
      return this;
    }

    public Builder onKill(ZombieEffect effect) {
      kill = effect;
      return this;
    }

    public Builder onKilled(ZombieEffect effect) {
      killed = effect;
      return this;
    }

    public ZombieEvents build() {
      return new ZombieEvents(spawn, tick, attack, damage, death, kill, killed);
    }
  }
}
