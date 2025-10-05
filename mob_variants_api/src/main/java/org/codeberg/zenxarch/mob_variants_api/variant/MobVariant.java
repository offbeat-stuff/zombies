package org.codeberg.zenxarch.mob_variants_api.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.impl.attachment.AttachmentRegistryImpl;
import net.minecraft.entity.VariantSelectorProvider;
import net.minecraft.entity.spawn.SpawnCondition;
import net.minecraft.entity.spawn.SpawnConditionSelectors;
import net.minecraft.entity.spawn.SpawnContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.codeberg.zenxarch.mob_variants_api.registry.MobRegistryKeys;

public record MobVariant(
    Map<AttachmentType<?>, Object> components,
    SpawnConditionSelectors spawnConditions,
    List<RegistryEntry<MobVariant>> template)
    implements VariantSelectorProvider<SpawnContext, SpawnCondition> {

  private static final Codec<Map<AttachmentType<?>, Object>> COMPONENT_CODEC =
      Codec.dispatchedMap(
          Identifier.CODEC.comapFlatMap(MobVariant::getAttachment, AttachmentType::identifier),
          AttachmentType::persistenceCodec);

  public static final Codec<RegistryEntry<MobVariant>> ENTRY_CODEC =
      RegistryFixedCodec.of(MobRegistryKeys.MOB_VARIANT);

  public static final Codec<MobVariant> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      COMPONENT_CODEC.fieldOf("components").forGetter(MobVariant::components),
                      SpawnConditionSelectors.CODEC
                          .fieldOf("spawnConditions")
                          .forGetter(MobVariant::spawnConditions),
                      Codecs.listOrSingle(ENTRY_CODEC)
                          .optionalFieldOf("template", List.of())
                          .forGetter(MobVariant::template))
                  .apply(instance, MobVariant::new));

  public MobVariant(
      Map<AttachmentType<?>, Object> components, SpawnConditionSelectors spawnConditions) {
    this(components, spawnConditions, List.of());
  }

  private static DataResult<AttachmentType<?>> getAttachment(Identifier id) {
    var attachmentType = AttachmentRegistryImpl.get(id);
    if (attachmentType == null) return DataResult.error(() -> "Attachment Type does not exist");
    if (attachmentType.persistenceCodec() == null)
      return DataResult.error(() -> "Attachment Type is not persistent", attachmentType);
    return DataResult.success(attachmentType);
  }

  @Override
  public List<Selector<SpawnContext, SpawnCondition>> getSelectors() {
    return this.spawnConditions.selectors();
  }
}
