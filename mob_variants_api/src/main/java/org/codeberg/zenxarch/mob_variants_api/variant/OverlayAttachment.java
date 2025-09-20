package org.codeberg.zenxarch.mob_variants_api.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.AssetInfo.TextureAssetInfo;
import net.minecraft.util.Identifier;

public record OverlayAttachment(Identifier layerId, String layerName, TextureAssetInfo texture) {

  public static final Codec<OverlayAttachment> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      Identifier.CODEC.fieldOf("layerId").forGetter(OverlayAttachment::layerId),
                      Codec.STRING.fieldOf("layerName").forGetter(OverlayAttachment::layerName),
                      TextureAssetInfo.CODEC
                          .fieldOf("texture")
                          .forGetter(OverlayAttachment::texture))
                  .apply(instance, OverlayAttachment::new));

  public static final PacketCodec<ByteBuf, OverlayAttachment> PACKET_CODEC =
      PacketCodec.of(
          (value, buff) -> {
            Identifier.PACKET_CODEC.encode(buff, value.layerId);
            PacketCodecs.STRING.encode(buff, value.layerName);
            TextureAssetInfo.PACKET_CODEC.encode(buff, value.texture);
          },
          buf ->
              new OverlayAttachment(
                  Identifier.PACKET_CODEC.decode(buf),
                  PacketCodecs.STRING.decode(buf),
                  TextureAssetInfo.PACKET_CODEC.decode(buf)));
}
