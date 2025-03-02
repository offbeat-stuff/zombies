package org.codeberg.zenxarch.zombies.registry;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect;
import org.codeberg.zenxarch.zombies.entity.variant.ZombieVariant;

public interface ZombieRegistries {

  private static <T> DefaultedRegistry<MapCodec<? extends T>> createRegistry(
      RegistryKey<Registry<MapCodec<? extends T>>> key) {
    return FabricRegistryBuilder.createDefaulted(key, Zombies.id("default")).buildAndRegister();
  }

  public static final DefaultedRegistry<MapCodec<? extends SingleLivingEffect>>
      SINGLE_LIVING_EFFECT_REGISTRY = createRegistry(ZombieRegistryKeys.SINGLE_LIVING);

  public static final DefaultedRegistry<MapCodec<? extends ZombieEffect>> ZOMBIE_EFFECT =
      createRegistry(ZombieRegistryKeys.ZOMBIE_EFFECT);

  public static void init() {
    ZombieEffect.init();
    SingleLivingEffect.init();
    DynamicRegistries.register(ZombieRegistryKeys.ZOMBIE_VARIANT, ZombieVariant.CODEC);
  }
}
