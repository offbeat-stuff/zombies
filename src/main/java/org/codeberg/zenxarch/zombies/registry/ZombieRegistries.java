package org.codeberg.zenxarch.zombies.registry;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.entity.ZombieTemplate;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.single.SingleLivingEffect;

public interface ZombieRegistries {

  private static <T> RegistryKey<Registry<T>> getRegistryKey(String id) {
    return RegistryKey.ofRegistry(Zombies.id(id));
  }

  private static <T> DefaultedRegistry<MapCodec<? extends T>> createRegistry(
      RegistryKey<Registry<MapCodec<? extends T>>> key) {
    return FabricRegistryBuilder.createDefaulted(key, Zombies.id("default")).buildAndRegister();
  }

  public static final RegistryKey<Registry<MapCodec<? extends SingleLivingEffect>>>
      SINGLE_LIVING_EFFECT_REGISTRY_KEY = getRegistryKey("single_living_effect");
  public static final RegistryKey<Registry<MapCodec<? extends ZombieEffect>>>
      ZOMBIE_EFFECT_REGISTRY_KEY = getRegistryKey("zombie_effect");

  public static final DefaultedRegistry<MapCodec<? extends SingleLivingEffect>>
      SINGLE_LIVING_EFFECT_REGISTRY = createRegistry(SINGLE_LIVING_EFFECT_REGISTRY_KEY);

  public static final DefaultedRegistry<MapCodec<? extends ZombieEffect>> ZOMBIE_EFFECT_REGISTRY =
      createRegistry(ZOMBIE_EFFECT_REGISTRY_KEY);

  public static final RegistryKey<Registry<ZombieTemplate>> TEMPLATE_REGISTRY_KEY =
      getRegistryKey("zombie_template");

  public static void init() {
    ZombieEffect.init();
    SingleLivingEffect.init();
    DynamicRegistries.register(TEMPLATE_REGISTRY_KEY, ZombieTemplate.CODEC);
  }
}
