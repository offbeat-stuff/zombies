package org.codeberg.zenxarch.zombies;

import static net.minecraft.command.argument.EntityArgumentType.*;
import static net.minecraft.command.argument.IdentifierArgumentType.*;
import static net.minecraft.server.command.CommandManager.*;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Map;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockPos;
import org.codeberg.zenxarch.zombies.data.entity.MobAttachments;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.ExtendedZombieEntity;
import org.codeberg.zenxarch.zombies.entity.variant.MobVariant;
import org.codeberg.zenxarch.zombies.registry.ZombieRegistryKeys;

public final class DebugCommands {
  private DebugCommands() {
    throw new IllegalStateException("Utility class");
  }

  static void registerDebugCommands(
      CommandDispatcher<ServerCommandSource> dispatcher,
      CommandRegistryAccess registryAccess,
      RegistrationEnvironment environment) {
    dispatcher.register(
        literal("zgiveloot")
            .then(
                argument("entity", entity())
                    .then(
                        argument("identifier", identifier()).executes(DebugCommands::zgiveloot))));

    dispatcher.register(
        literal("zspawnvariant")
            .then(
                argument("mob_variant", mobVariant(registryAccess))
                    .executes(DebugCommands::zspawnvariant)));
  }

  private static Map<EquipmentSlot, Float> createSlotDropChances(
      List<EquipmentSlot> slots, float dropChance) {
    Map<EquipmentSlot, Float> map = Maps.<EquipmentSlot, Float>newHashMap();

    for (EquipmentSlot equipmentSlot : slots) {
      map.put(equipmentSlot, dropChance);
    }

    return map;
  }

  private static int zgiveloot(CommandContext<ServerCommandSource> ctx)
      throws CommandSyntaxException {
    var entity = EntityArgumentType.getEntity(ctx, "entity");
    var id = IdentifierArgumentType.getIdentifier(ctx, "identifier");
    if (entity instanceof MobEntity mob) {
      MobAttachments.initEquipment(
          mob,
          ctx.getSource().getWorld(),
          new EquipmentTable(
              RegistryKey.of(RegistryKeys.LOOT_TABLE, id),
              createSlotDropChances(List.of(EquipmentSlot.values()), 1.0f)),
          new ExtendedDifficulty(ctx.getSource().getWorld(), mob.getBlockPos()));
    }
    return 0;
  }

  private static int zspawnvariant(CommandContext<ServerCommandSource> ctx)
      throws CommandSyntaxException {
    var world = ctx.getSource().getWorld();
    var pos = ctx.getSource().getPosition();
    var zombie = new ExtendedZombieEntity(world);
    zombie.refreshPositionAndAngles(pos, world.random.nextFloat() * 360.0F, 0.0F);
    zombie.initialize(
        world,
        world.getLocalDifficulty(BlockPos.ofFloored(pos)),
        SpawnReason.COMMAND,
        new ExtendedZombieEntity.ExtendedZombieData(
            MobVariantArgumentType.getVariant(ctx, "mob_variant"), false, false));
    world.spawnEntityAndPassengers(zombie);
    return 0;
  }

  public static MobVariantArgumentType mobVariant(CommandRegistryAccess registryAccess) {
    return new MobVariantArgumentType(registryAccess);
  }

  public static class MobVariantArgumentType extends RegistryEntryArgumentType<MobVariant> {

    public MobVariantArgumentType(CommandRegistryAccess registryAccess) {
      super(
          registryAccess,
          ZombieRegistryKeys.MOB_VARIANT,
          RegistryElementCodec.of(ZombieRegistryKeys.MOB_VARIANT, MobVariant.CODEC));
    }

    @SuppressWarnings("unchecked")
    public static RegistryEntry<MobVariant> getVariant(
        CommandContext<ServerCommandSource> context, String argument) {
      return context.getArgument(argument, RegistryEntry.class);
    }
  }
}
