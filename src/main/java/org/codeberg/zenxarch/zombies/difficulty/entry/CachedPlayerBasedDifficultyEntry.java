package org.codeberg.zenxarch.zombies.difficulty.entry;

import java.util.function.BiFunction;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.codeberg.zenxarch.zombies.difficulty.CachedValue;

public record CachedPlayerBasedDifficultyEntry(
    AttachmentType<CachedValue> attachment,
    BiFunction<ServerWorld, ServerPlayerEntity, Double> mapper,
    int weight)
    implements PlayerBasedDifficultyEntry {

  @Override
  public int getWeight() {
    return weight;
  }

  @Override
  public double calculate(ServerWorld world, ServerPlayerEntity player) {
    return CachedValue.getOrUpdateValue(world, player, attachment, mapper);
  }
}
