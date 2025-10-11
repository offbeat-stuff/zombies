package org.codeberg.zenxarch.zombies;

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
    if (!world.getGameRules().getBoolean(ZombieGamerules.DO_ZOMBIE_KILLS_BASED_HEARTS)) return;
    var health = player.getHealth();
    var maxHealth = player.getMaxHealth();
    var kills = player.getAttachedOrCreate(ZOMBIE_KILLS);
    var zombiesToKill =
        (float) world.getGameRules().getInt(ZombieGamerules.ZOMBIE_KILLS_FOR_MAX_HEARTS);
    var additionalHalfHearts = (int) (MathHelper.clamp(kills / zombiesToKill, 0f, 44f) * 2f);
    Zombies.LOGGER.info("Additional Hearts: {}", additionalHalfHearts);
    var newHealth = MathHelper.clamp(6f + additionalHalfHearts / 2f, 6f, 50f);
    player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(newHealth);
    player.setHealth(health * newHealth / maxHealth);
  }

  private static void onKillZombie(ServerPlayerEntity player) {
    var kills = player.getAttachedOrCreate(ZOMBIE_KILLS);
    player.setAttached(ZOMBIE_KILLS, kills + 1);
  }
}
