package org.codeberg.zenxarch.zombies.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobEntity.class)
public interface MobEntityAccessor {
  @Accessor("lootTable")
  public void setLootTable(RegistryKey<LootTable> lootTable);
}
