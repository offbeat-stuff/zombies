package org.codeberg.zenxarch.zombies.entity.variant;

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
import net.minecraft.util.Identifier;

public record ZombieVariant(
    Map<AttachmentType<?>, Object> components, SpawnConditionSelectors spawnConditions)
    implements VariantSelectorProvider<SpawnContext, SpawnCondition> {

  private static final Codec<Map<AttachmentType<?>, Object>> COMPONENT_CODEC =
      Codec.dispatchedMap(
          Identifier.CODEC.comapFlatMap(ZombieVariant::getAttachment, AttachmentType::identifier),
          AttachmentType::persistenceCodec);

  public static final Codec<ZombieVariant> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      COMPONENT_CODEC.fieldOf("components").forGetter(ZombieVariant::components),
                      SpawnConditionSelectors.CODEC
                          .fieldOf("spawnConditions")
                          .forGetter(ZombieVariant::spawnConditions))
                  .apply(instance, ZombieVariant::new));

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
