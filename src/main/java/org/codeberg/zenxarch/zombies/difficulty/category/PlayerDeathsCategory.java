package org.codeberg.zenxarch.zombies.difficulty.category;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.util.List;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.CachedValue;
import org.codeberg.zenxarch.zombies.difficulty.DifficultyCategory;
import org.codeberg.zenxarch.zombies.difficulty.entry.CachedPlayerBasedDifficultyEntry;

public interface PlayerDeathsCategory {

  public static final AttachmentType<CachedValue> DEATH =
      CachedValue.createAttachmentType(Zombies.id("player_deaths/death_multiplier"));

  public static final CachedPlayerBasedDifficultyEntry DEATH_ENTRY =
      new CachedPlayerBasedDifficultyEntry(DEATH, PlayerDeathsCategory::getDifficulty, 1);

  public static final Identifier PLAYER_DEATHS_CATEGORY = Zombies.id("player_deaths");

  public static void initialize() {
    DifficultyCategory.addDifficultyEntry(PLAYER_DEATHS_CATEGORY, DEATH_ENTRY);
    ServerLivingEntityEvents.AFTER_DEATH.register(
        (entity, source) -> {
          if (entity instanceof ServerPlayerEntity player) recordPlayerDeath(player, source);
        });
  }

  public static final long TIME_WINDOW = 20 * 60 * 15;
  public static final int MAX_DEATHS = 10;

  public static final AttachmentType<List<Long>> DEATH_TIMESTAMPS =
      AttachmentRegistry.create(
          Zombies.id("player_death_timestamps"),
          builder ->
              builder
                  .persistent(Codec.list(Codec.LONG))
                  .initializer(() -> new LongArrayList(MAX_DEATHS))
                  .copyOnDeath());

  private static void recordPlayerDeath(ServerPlayerEntity player, DamageSource source) {
    var time = player.getEntityWorld().getTime();
    var deaths = player.getAttachedOrCreate(DEATH_TIMESTAMPS);
    deaths.add(time);
    trimDeaths(deaths, time);
  }

  private static int getAndUpdateDeathCount(ServerPlayerEntity player) {
    return trimDeaths(
        player.getAttachedOrCreate(DEATH_TIMESTAMPS), player.getEntityWorld().getTime());
  }

  private static int trimDeaths(List<Long> deaths, long time) {
    var deathCount = countRecentDeaths(deaths, time);
    while (deaths.size() > deathCount) deaths.removeFirst();
    return deathCount;
  }

  private static int countRecentDeaths(List<Long> deaths, long time) {
    var size = Math.min(deaths.size(), MAX_DEATHS);
    for (int i = 0; i < size; i++)
      if ((time - deaths.get(deaths.size() - 1 - i)) > TIME_WINDOW) return i;
    return size;
  }

  private static double mapDeathsToDifficulty(int deathCount) {
    return MathHelper.clampedMap(deathCount, 3, MAX_DEATHS, 1.0, 0.25);
  }

  private static double getDifficulty(ServerWorld world, ServerPlayerEntity player) {
    return mapDeathsToDifficulty(getAndUpdateDeathCount(player));
  }
}
