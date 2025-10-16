package org.codeberg.zenxarch.zombies.difficulty;

import java.util.function.BiFunction;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.world.ServerWorld;

public record CachedValue(double value, long lastUpdated) {
  public static <T extends AttachmentTarget> double getOrUpdateValue(
      ServerWorld world,
      T target,
      AttachmentType<CachedValue> attachment,
      BiFunction<ServerWorld, T, Double> value) {
    var time = world.getTime();
    var attached =
        target.getAttachedOrCreate(
            attachment, () -> new CachedValue(value.apply(world, target), world.getTime()));
    if (attached.lastUpdated >= time) return attached.value;
    var newValue = value.apply(world, target);
    target.setAttached(attachment, new CachedValue(newValue, time));
    return newValue;
  }
}
