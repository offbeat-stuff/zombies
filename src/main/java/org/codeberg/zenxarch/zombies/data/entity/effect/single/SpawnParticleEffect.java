package org.codeberg.zenxarch.zombies.data.entity.effect.single;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.enchantment.effect.entity.SpawnParticlesEnchantmentEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;

public record SpawnParticleEffect(SpawnParticlesEnchantmentEffect effect, int count)
    implements LivingEffect {

  public static final MapCodec<SpawnParticleEffect> CODEC =
      Codec.mapPair(
              SpawnParticlesEnchantmentEffect.CODEC,
              Codecs.POSITIVE_INT.optionalFieldOf("count", 0))
          .xmap(
              pair -> new SpawnParticleEffect(pair.getFirst(), pair.getSecond()),
              effect -> new Pair<>(effect.effect, effect.count));

  public SpawnParticleEffect(SpawnParticlesEnchantmentEffect effect) {
    this(effect, 0);
  }

  @Override
  public void run(ServerWorld world, LivingEntity target) {
    var pos = target.getPos();
    var random = target.getRandom();
    var move = target.getMovement();
    var width = target.getWidth();
    var height = target.getHeight();
    var boundingBoxCenter = pos.getY() + height / 2;
    world.spawnParticles(
        this.effect.particle(),
        this.effect.horizontalPosition().getPosition(pos.getX(), pos.getX(), width, random),
        this.effect.verticalPosition().getPosition(pos.getY(), boundingBoxCenter, height, random),
        this.effect.horizontalPosition().getPosition(pos.getZ(), pos.getZ(), width, random),
        this.count,
        this.effect.horizontalVelocity().getVelocity(move.getX(), random),
        this.effect.verticalVelocity().getVelocity(move.getY(), random),
        this.effect.horizontalVelocity().getVelocity(move.getZ(), random),
        this.effect.speed().get(random));
  }

  @Override
  public MapCodec<SpawnParticleEffect> getCodec() {
    return CODEC;
  }
}
