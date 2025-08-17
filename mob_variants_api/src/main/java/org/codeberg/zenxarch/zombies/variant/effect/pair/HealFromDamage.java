package org.codeberg.zenxarch.zombies.variant.effect.pair;

import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.mixin.LivingEntityAccessor;
import org.codeberg.zenxarch.zombies.variant.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

public record HealFromDamage() implements MobEffect {

  public static final HealFromDamage INSTANCE = new HealFromDamage();
  public static final MapCodec<HealFromDamage> CODEC = MapCodec.unit(INSTANCE);

  @Override
  public void run(ServerWorld world, MobEntity mob, @Nullable LivingEntity adversery) {
    if (adversery == null) return;
    var damageSource = adversery.getRecentDamageSource();
    if (!damageSource.getAttacker().equals(mob) || !damageSource.isDirect()) return;
    mob.heal(((LivingEntityAccessor) adversery).getLastDamageTaken());
  }

  @Override
  public MapCodec<? extends MobEffect> getCodec() {
    return CODEC;
  }
}
