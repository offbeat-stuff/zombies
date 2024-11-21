package org.codeberg.zenxarch.zombies.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.TeleportTarget;

public interface AttackEffect {
  void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked);

  public static record IgniteAttackEffect() implements AttackEffect {
    @Override
    public void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked) {
      attacked.setOnFireFor(1.0F);
    }
  }

  public static record FreezeAttackEffect() implements AttackEffect {
    @Override
    public void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked) {
      if (!attacked.canFreeze()) return;
      attacked.setFrozenTicks(attacked.getMinFreezeDamageTicks());
    }
  }

  public static record StatusEffectAttackEffect(RegistryEntry<StatusEffect> effect)
      implements AttackEffect {
    @Override
    public void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked) {
      float f = world.getLocalDifficulty(attacked.getBlockPos()).getLocalDifficulty();
      attacked.addStatusEffect(new StatusEffectInstance(effect, 140 * (int) f), zombie);
    }
  }

  public static record SwapPositionsAttackEffect() implements AttackEffect {
    @Override
    public void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked) {
      var zTarget =
          new TeleportTarget(
              world,
              zombie.getPos(),
              zombie.getVelocity(),
              zombie.getYaw(),
              zombie.getPitch(),
              TeleportTarget.NO_OP);
      var aTarget =
          new TeleportTarget(
              world,
              attacked.getPos(),
              attacked.getVelocity(),
              attacked.getYaw(),
              attacked.getPitch(),
              TeleportTarget.NO_OP);
      zombie.teleportTo(aTarget);
      attacked.teleportTo(zTarget);
    }
  }
}
