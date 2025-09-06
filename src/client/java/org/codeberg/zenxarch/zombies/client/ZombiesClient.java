package org.codeberg.zenxarch.zombies.client;

import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

public class ZombiesClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    EntityRendererRegistry.register(EntityType.ZOMBIE, ExtendedZombieEntityRenderer::new);
    var doors =
        List.of(
            Items.IRON_DOOR,
            Items.OAK_DOOR,
            Items.SPRUCE_DOOR,
            Items.BIRCH_DOOR,
            Items.JUNGLE_DOOR,
            Items.ACACIA_DOOR,
            Items.CHERRY_DOOR,
            Items.DARK_OAK_DOOR,
            Items.PALE_OAK_DOOR,
            Items.MANGROVE_DOOR,
            Items.BAMBOO_DOOR,
            Items.CRIMSON_DOOR,
            Items.WARPED_DOOR,
            Items.COPPER_DOOR,
            Items.EXPOSED_COPPER_DOOR,
            Items.WEATHERED_COPPER_DOOR,
            Items.OXIDIZED_COPPER_DOOR,
            Items.WAXED_COPPER_DOOR,
            Items.WAXED_EXPOSED_COPPER_DOOR,
            Items.WAXED_WEATHERED_COPPER_DOOR,
            Items.WAXED_OXIDIZED_COPPER_DOOR);
    for (var door : doors) ArmorRenderer.register(new DoorShieldRenderer(), door);
  }
}
