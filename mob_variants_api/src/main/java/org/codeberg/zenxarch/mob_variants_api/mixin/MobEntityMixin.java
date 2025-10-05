package org.codeberg.zenxarch.mob_variants_api.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements AttachmentTarget {

  @ModifyReturnValue(method = "getLootTableKey", at = @At("RETURN"))
  private Optional<RegistryKey<LootTable>> zenxarch$override_loot_table(
      Optional<RegistryKey<LootTable>> lootTable) {
    if (this.hasAttached(MobAttachments.LOOT_TABLE))
      return Optional.of(this.getAttached(MobAttachments.LOOT_TABLE));
    return lootTable;
  }

  @Inject(method = "setTarget", at = @At("TAIL"))
  private void zenxarch$setTargetHook(@Nullable LivingEntity target, CallbackInfo ci) {
    var self = (MobEntity) (Object) this;
    if (!(self.getEntityWorld() instanceof ServerWorld world)) return;
    if (target == null) {
      if (self.hasAttached(MobAttachments.ON_REMOVE_TARGET))
        self.getAttached(MobAttachments.ON_REMOVE_TARGET).run(world, self, target);
    } else {
      if (self.hasAttached(MobAttachments.ON_ADD_TARGET))
        self.getAttached(MobAttachments.ON_ADD_TARGET).run(world, self, target);
    }
  }
}
