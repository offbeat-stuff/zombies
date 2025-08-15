package org.codeberg.zenxarch.zombies.data.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

public record OverlayAttachment(Identifier layerId, String layerName, AssetInfo texture) {

  public static final Codec<OverlayAttachment> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Identifier.CODEC.fieldOf("layerId").forGetter(OverlayAttachment::layerId),
                      Codec.STRING.fieldOf("layerName").forGetter(OverlayAttachment::layerName),
                      AssetInfo.CODEC.fieldOf("texture").forGetter(OverlayAttachment::texture))
                  .apply(instance, OverlayAttachment::new));

  public static final PacketCodec<ByteBuf, OverlayAttachment> PACKET_CODEC =
      PacketCodec.of(
          (value, buff) -> {
            Identifier.PACKET_CODEC.encode(buff, value.layerId);
            PacketCodecs.STRING.encode(buff, value.layerName);
            AssetInfo.PACKET_CODEC.encode(buff, value.texture);
          },
          buf ->
              new OverlayAttachment(
                  Identifier.PACKET_CODEC.decode(buf),
                  PacketCodecs.STRING.decode(buf),
                  AssetInfo.PACKET_CODEC.decode(buf)));

  @Environment(EnvType.CLIENT)
  public OverlayAttachment(EntityModelLayer layer, AssetInfo info) {
    this(layer.id(), layer.name(), info);
  }

  @Environment(EnvType.CLIENT)
  public EntityModelLayer getEntityModelLayer() {
    return new EntityModelLayer(this.layerId, this.layerName);
  }
}
