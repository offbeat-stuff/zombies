package org.codeberg.zenxarch.zombies.registry;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.data.entity.effect.LivingEffect;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;
import org.codeberg.zenxarch.zombies.entity.variant.MobVariant;

public interface ZombieRegistries {

  private static <T> DefaultedRegistry<MapCodec<? extends T>> createRegistry(
      RegistryKey<Registry<MapCodec<? extends T>>> key) {
    return FabricRegistryBuilder.createDefaulted(key, Zombies.id("default")).buildAndRegister();
  }

  public static final DefaultedRegistry<MapCodec<? extends LivingEffect>> LIVING_EFFECT =
      createRegistry(ZombieRegistryKeys.LIVING_EFFECT);

  public static final DefaultedRegistry<MapCodec<? extends MobEffect>> MOB_EFFECT =
      createRegistry(ZombieRegistryKeys.MOB_EFFECT);

  public static void init() {
    MobEffect.init();
    LivingEffect.init();
    DynamicRegistries.register(ZombieRegistryKeys.MOB_VARIANT, MobVariant.CODEC);
  }
}
