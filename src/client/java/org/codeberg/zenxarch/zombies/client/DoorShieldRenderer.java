package org.codeberg.zenxarch.zombies.client;

import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class DoorShieldRenderer implements ArmorRenderer {

  private ItemRenderState state = new ItemRenderState();

  @Override
  public void render(
      MatrixStack matrices,
      OrderedRenderCommandQueue orderedRenderCommandQueue,
      ItemStack stack,
      BipedEntityRenderState bipedEntityRenderState,
      EquipmentSlot slot,
      int light,
      BipedEntityModel<BipedEntityRenderState> contextModel) {
    matrices.push();

    var client = MinecraftClient.getInstance();
    client
        .getItemModelManager()
        .clearAndUpdate(state, stack, ItemDisplayContext.HEAD, client.world, null, light);

    contextModel.getRootPart().applyTransform(matrices);

    matrices.translate(0.0F, 1.3F, -1.48F);
    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
    matrices.scale(1.75F, -1.75F, -1.75F);

    state.render(
        matrices,
        orderedRenderCommandQueue,
        light,
        OverlayTexture.DEFAULT_UV,
        bipedEntityRenderState.outlineColor);

    matrices.pop();
  }

  @Override
  public boolean shouldRenderDefaultHeadItem(LivingEntity entity, ItemStack stack) {
    return false;
  }
}
