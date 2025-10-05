package org.codeberg.zenxarch.mob_variants_api.variant;

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
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import net.minecraft.world.LocalDifficulty;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;
import org.codeberg.zenxarch.mob_variants_api.variant.effect.MobEffect;

public final class MobAttachments {
  private MobAttachments() {
    throw new IllegalStateException("Utility class");
  }

  public static final AttachmentType<TextureAssetInfo> TEXTURE_OVERRIDE =
      createAttachment("texture_override", TextureAssetInfo.CODEC, TextureAssetInfo.PACKET_CODEC);

  public static final AttachmentType<EquipmentTable> EQUIPMENT_TABLE =
      createAttachment("equipment_table", EquipmentTable.CODEC);

  public static final AttachmentType<RegistryKey<LootTable>> LOOT_TABLE =
      createAttachment("loot_table", RegistryKey.createCodec(RegistryKeys.LOOT_TABLE));

  public static final AttachmentType<RegistryEntryList<DamageType>> INVULNERABLE_TO =
      createAttachment("invulnerable_to", RegistryCodecs.entryList(RegistryKeys.DAMAGE_TYPE));

  public static final AttachmentType<OverlayAttachment> OVERLAY =
      createAttachment("overlay", OverlayAttachment.CODEC, OverlayAttachment.PACKET_CODEC);

  public static final AttachmentType<MobEffect> ON_SPAWN = createMobEvent("on_spawn");
  public static final AttachmentType<MobEffect> ON_TICK = createMobEvent("on_tick");
  public static final AttachmentType<MobEffect> ON_ATTACK = createMobEvent("on_attack");
  public static final AttachmentType<MobEffect> ON_DAMAGE = createMobEvent("on_damage");
  public static final AttachmentType<MobEffect> ON_DEATH = createMobEvent("on_death");
  public static final AttachmentType<MobEffect> ON_KILL = createMobEvent("on_kill");
  public static final AttachmentType<MobEffect> ON_KILLED = createMobEvent("on_killed");
  public static final AttachmentType<MobEffect> ON_ADD_TARGET = createMobEvent("on_add_target");
  public static final AttachmentType<MobEffect> ON_REMOVE_TARGET =
      createMobEvent("on_remove_target");

  public static final AttachmentType<Boolean> RENDER_HEAD =
      createAttachment("render_head", Codec.BOOL, PacketCodecs.BOOLEAN);

  private static AttachmentType<MobEffect> createMobEvent(String id) {
    return AttachmentRegistry.createPersistent(MobVariantsApiMod.id(id), MobEffect.CODEC);
  }

  private static <T> AttachmentType<T> createAttachment(String id, Codec<T> codec) {
    return AttachmentRegistry.createPersistent(MobVariantsApiMod.id(id), codec);
  }

  private static <T> AttachmentType<T> createAttachment(
      String id, Codec<T> codec, PacketCodec<? super RegistryByteBuf, T> packetCodec) {
    return AttachmentRegistry.create(
        MobVariantsApiMod.id(id),
        builder -> builder.persistent(codec).syncWith(packetCodec, AttachmentSyncPredicate.all()));
  }

  public static void initEquipment(
      MobEntity mob, ServerWorld world, EquipmentTable table, LocalDifficulty difficulty) {
    mob.setEquipmentFromTable(
        table.lootTable(),
        new LootWorldContext.Builder(world)
            .add(LootContextParameters.ORIGIN, mob.getEntityPos())
            .add(LootContextParameters.THIS_ENTITY, mob)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT),
        table.slotDropChances());
  }

  public static void initialize() {
    // Force load class
  }
}
