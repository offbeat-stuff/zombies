package org.codeberg.zenxarch.zombies.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.tag.TagKey;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

  @WrapOperation(
      method = "tickMovement",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/entity/EntityType;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
  private boolean zenxarch$modifyBurnInDaylight(
      EntityType<?> type, TagKey<EntityType<?>> tag, Operation<Boolean> op) {
    if (((Object) this) instanceof ExtendedZombieEntity zombie) return zombie.burnsInDaylight();
    return op.call(type, tag);
  }
}
