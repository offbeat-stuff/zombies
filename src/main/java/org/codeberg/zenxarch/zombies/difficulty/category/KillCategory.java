package org.codeberg.zenxarch.zombies.difficulty.category;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.zombies.ZombieHealth;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.CachedValue;
import org.codeberg.zenxarch.zombies.difficulty.DifficultyCategory;
import org.codeberg.zenxarch.zombies.difficulty.entry.CachedPlayerBasedDifficultyEntry;

public interface KillCategory {
  private static AttachmentType<CachedValue> createCache(String id) {
    return AttachmentRegistry.create(Zombies.id(id));
  }

  public static final AttachmentType<CachedValue> ZOMBIE_KILLS = createCache("kills/zombie_kills");
  public static final AttachmentType<CachedValue> ZOMBIE_KILLS_SINCE_RESPAWN =
      createCache("kills/zombie_kills_since_respawn");

  public static final CachedPlayerBasedDifficultyEntry ZOMBIE_KILLS_ENTRY =
      new CachedPlayerBasedDifficultyEntry(ZOMBIE_KILLS, KillCategory::zombieKillScore, 10);

  public static final CachedPlayerBasedDifficultyEntry ZOMBIE_KILLS_SINCE_RESPAWN_ENTRY =
      new CachedPlayerBasedDifficultyEntry(
          ZOMBIE_KILLS_SINCE_RESPAWN, KillCategory::zombieKillScoreSinceRespawn, 1);

  public static final Identifier KILL_CATEGORY = Zombies.id("kills");

  public static void initialize() {
    DifficultyCategory.addDifficultyEntry(KILL_CATEGORY, ZOMBIE_KILLS_ENTRY);
    DifficultyCategory.addDifficultyEntry(KILL_CATEGORY, ZOMBIE_KILLS_SINCE_RESPAWN_ENTRY);
  }

  private static double zombieKillScore(ServerWorld world, ServerPlayerEntity player) {
    return Registries.ENTITY_TYPE
        .getOptional(EntityTypeTags.ZOMBIES)
        .map(
            list ->
                list.stream().mapToDouble(type -> getKillStat(player, type.value())).sum() / 2500)
        .orElse(0.0);
  }

  private static double zombieKillScoreSinceRespawn(ServerWorld world, ServerPlayerEntity player) {
    return player.getAttachedOrCreate(ZombieHealth.ZOMBIE_KILLS) / 2500.0;
  }

  private static int getKillStat(ServerPlayerEntity player, EntityType<?> type) {
    return player.getStatHandler().getStat(Stats.KILLED.getOrCreateStat(type));
  }
}
