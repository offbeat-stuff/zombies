package org.codeberg.zenxarch.zombies;

import static org.codeberg.zenxarch.zombies.ZombieGamerules.*;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public interface ZombieHealth {

  public static final AttachmentType<Integer> ZOMBIE_KILLS =
      AttachmentRegistry.create(
          Zombies.id("zombie_kills"),
          builder -> builder.persistent(Codec.INT).initializer(() -> 0));

  public static void registerEvents() {
    ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(
        (world, self, dead, source) -> {
          if (self instanceof ServerPlayerEntity player
              && dead.getType().isIn(EntityTypeTags.ZOMBIES)) {
            onKillZombie(player);
            updatePlayerStats(world, player);
          }
        });

    ServerPlayerEvents.JOIN.register(
        (player) -> updatePlayerStats(player.getEntityWorld(), player));

    ServerPlayerEvents.AFTER_RESPAWN.register(
        (oldPlayer, newPlayer, alive) -> updatePlayerStats(newPlayer.getEntityWorld(), newPlayer));
  }

  private static void updatePlayerStats(ServerWorld world, ServerPlayerEntity player) {
    var rules = world.getGameRules();
    if (!rules.getBoolean(DO_ZOMBIE_KILLS_BASED_HEARTS)) return;

    var newMaxHealth =
        getNewMaxHealth(
            player.getAttachedOrCreate(ZOMBIE_KILLS),
            rules.getInt(ZOMBIE_KILLS_FOR_MAX_HEARTS),
            rules.getInt(MIN_HEARTS),
            rules.getInt(MAX_HEARTS));

    var newHealth = (player.getHealth() / player.getMaxHealth()) * newMaxHealth;

    player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(newMaxHealth);
    player.setHealth(newHealth);
  }

  private static float getNewMaxHealth(int kills, int maxKills, int minHearts, int maxHearts) {
    var delta = (float) kills / maxKills;
    return MathHelper.clamp(MathHelper.lerp(delta, minHearts, maxHearts), minHearts, maxHearts)
        * 2f;
  }

  private static void onKillZombie(ServerPlayerEntity player) {
    var kills = player.getAttachedOrCreate(ZOMBIE_KILLS);
    player.setAttached(ZOMBIE_KILLS, kills + 1);
  }
}
