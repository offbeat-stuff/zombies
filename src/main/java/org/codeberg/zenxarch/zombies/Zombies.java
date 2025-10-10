package org.codeberg.zenxarch.zombies;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;
import org.codeberg.zenxarch.zombies.loot_table.condition.ZombieLootConditionTypes;
import org.codeberg.zenxarch.zombies.loot_table.function.ZombieLootFunctionTypes;
import org.codeberg.zenxarch.zombies.loot_table.number_provider.ZombieLootNumberProviderTypes;
import org.codeberg.zenxarch.zombies.spawning.SpawnerAttachments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Zombies implements ModInitializer {

  public static final String MODID = "zombies_zenxarch";
  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }

  @Override
  public void onInitialize() {
    LOGGER.info("Hello Fabric world!");
    ZombieGamerules.initialize();
    ZombieLootNumberProviderTypes.initialize();
    ZombieLootFunctionTypes.initialize();
    ZombieLootConditionTypes.initialize();

    if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
      ArgumentTypeRegistry.registerArgumentType(
          id("mob_variant"),
          DebugCommands.MobVariantArgumentType.class,
          ConstantArgumentSerializer.of(DebugCommands::mobVariant));
      CommandRegistrationCallback.EVENT.register(DebugCommands::registerDebugCommands);
    }

    SpawnerAttachments.initialize();

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

  public static final AttachmentType<Integer> ZOMBIE_KILLS =
      AttachmentRegistry.create(
          id("zombie_kills"),
          builder -> builder.persistent(Codecs.POSITIVE_INT).initializer(() -> 0));

  private static void updatePlayerStats(ServerWorld world, ServerPlayerEntity player) {
    var health = player.getHealth();
    var maxHealth = player.getMaxHealth();
    var kills = player.getAttachedOrCreate(ZOMBIE_KILLS);
    var newHealth = MathHelper.clamp(6f + MathHelper.lerp(kills / 2500f, 0f, 44f), 6f, 50f);
    player.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(newHealth);
    player.setHealth(health * newHealth / maxHealth);
  }

  private static void onKillZombie(ServerPlayerEntity player) {
    var kills = player.getAttachedOrCreate(ZOMBIE_KILLS);
    player.setAttached(ZOMBIE_KILLS, kills + 1);
  }
}
