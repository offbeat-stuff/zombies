package org.codeberg.zenxarch.zombies.data.entity;

import static org.codeberg.zenxarch.zombies.Zombies.id;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.AssetInfo;
import net.minecraft.world.LocalDifficulty;
import org.codeberg.zenxarch.zombies.data.entity.effect.MobEffect;

public final class MobAttachments {
  private MobAttachments() {
    throw new IllegalStateException("Utility class");
  }

  public static final AttachmentType<AssetInfo> TEXTURE_OVERRIDE =
      AttachmentRegistry.create(
          id("texture_override"),
          builder ->
              builder
                  .persistent(AssetInfo.CODEC)
                  .syncWith(AssetInfo.PACKET_CODEC, AttachmentSyncPredicate.all()));

  public static final AttachmentType<EquipmentTable> EQUIPMENT_TABLE =
      AttachmentRegistry.createPersistent(id("equipment_table"), EquipmentTable.CODEC);

  public static final AttachmentType<RegistryKey<LootTable>> LOOT_TABLE =
      AttachmentRegistry.createPersistent(
          id("loot_table"), RegistryKey.createCodec(RegistryKeys.LOOT_TABLE));

  public static final AttachmentType<RegistryEntryList<DamageType>> INVULNERABLE_TO =
      AttachmentRegistry.createPersistent(
          id("invulnerable_to"), RegistryCodecs.entryList(RegistryKeys.DAMAGE_TYPE));

  public static final AttachmentType<OverlayAttachment> OVERLAY =
      AttachmentRegistry.create(
          id("overlay"),
          builder ->
              builder
                  .persistent(OverlayAttachment.CODEC)
                  .syncWith(OverlayAttachment.PACKET_CODEC, AttachmentSyncPredicate.all()));

  public static final AttachmentType<MobEffect> ON_SPAWN = createZombieEvent("on_spawn");
  public static final AttachmentType<MobEffect> ON_TICK = createZombieEvent("on_tick");
  public static final AttachmentType<MobEffect> ON_ATTACK = createZombieEvent("on_attack");
  public static final AttachmentType<MobEffect> ON_DAMAGE = createZombieEvent("on_damage");
  public static final AttachmentType<MobEffect> ON_DEATH = createZombieEvent("on_death");
  public static final AttachmentType<MobEffect> ON_KILL = createZombieEvent("on_kill");
  public static final AttachmentType<MobEffect> ON_KILLED = createZombieEvent("on_killed");

  public static final AttachmentType<Boolean> RENDER_HEAD =
      AttachmentRegistry.createPersistent(id("render_head"), Codec.BOOL);

  private static AttachmentType<MobEffect> createZombieEvent(String id) {
    return AttachmentRegistry.createPersistent(id(id), MobEffect.CODEC);
  }

  public static void initEquipment(
      MobEntity mob, ServerWorld world, EquipmentTable table, LocalDifficulty difficulty) {
    mob.setEquipmentFromTable(
        table.lootTable(),
        new LootWorldContext.Builder(world)
            .add(LootContextParameters.ORIGIN, mob.getPos())
            .add(LootContextParameters.THIS_ENTITY, mob)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT),
        table.slotDropChances());
  }

  public static void initialize() {
    // Force load class
  }
}
