package org.codeberg.zenxarch.mob_variants_api.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import java.util.Optional;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import org.codeberg.zenxarch.mob_variants_api.variant.MobAttachments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin implements AttachmentTarget {

  @ModifyReturnValue(method = "getLootTableKey", at = @At("RETURN"))
  private Optional<RegistryKey<LootTable>> zenxarch$override_loot_table(
      Optional<RegistryKey<LootTable>> lootTable) {
    if (this.hasAttached(MobAttachments.LOOT_TABLE))
      return Optional.of(this.getAttached(MobAttachments.LOOT_TABLE));
    return lootTable;
  }
}
