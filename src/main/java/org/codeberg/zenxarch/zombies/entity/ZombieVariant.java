package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.BiomePredicate.TagSetEntry;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public record ZombieVariant(
    LootTableInfo lootTableInfo,
    ZombieEvents events,
    BiomePredicate biomePredicate,
    Optional<LootCondition> requirements,
    ZombieAttributes attributes,
    SpawnWeight weight) {

  public static final Codec<ZombieVariant> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      LootTableInfo.CODEC
                          .optionalFieldOf("lootTableInfo", LootTableInfo.DEFAULT)
                          .forGetter(ZombieVariant::lootTableInfo),
                      ZombieEvents.CODEC
                          .optionalFieldOf("events", ZombieEvents.DEFAULT)
                          .forGetter(ZombieVariant::events),
                      BiomePredicate.CODEC
                          .optionalFieldOf("biomePredicate", BiomePredicate.DEFAULT)
                          .forGetter(ZombieVariant::biomePredicate),
                      EnchantmentEffectEntry.createRequirementsCodec(LootContextTypes.EQUIPMENT)
                          .optionalFieldOf("requirements")
                          .forGetter(ZombieVariant::requirements),
                      ZombieAttributes.CODEC
                          .optionalFieldOf("attributes", ZombieAttributes.DEFAULT)
                          .forGetter(ZombieVariant::attributes),
                      SpawnWeight.CODEC
                          .optionalFieldOf("weight", SpawnWeight.DEFAULT)
                          .forGetter(ZombieVariant::weight))
                  .apply(instance, ZombieVariant::new));

  public void initEquipment(
      ServerWorld world, ExtendedZombieEntity zombie, ExtendedDifficulty difficulty) {
    lootTableInfo.initEquipment(world, zombie, difficulty);
  }

  public int getWeight(ExtendedDifficulty difficulty) {
    return weight.get(difficulty);
  }

  public boolean canSpawnIn(RegistryEntry<Biome> biome) {
    return biomePredicate.test(biome);
  }

  public boolean canSpawnAt(
      ServerWorld world, BlockPos pos, ServerPlayerEntity player, ExtendedDifficulty difficulty) {
    return requirements.map(r -> canSpawnAt(r, world, pos, player, difficulty)).orElse(true);
  }

  private static boolean canSpawnAt(
      LootCondition requirements,
      ServerWorld world,
      BlockPos pos,
      ServerPlayerEntity player,
      ExtendedDifficulty difficulty) {
    var lootContext =
        new LootWorldContext.Builder(world)
            .add(LootContextParameters.ORIGIN, pos.toBottomCenterPos())
            .add(LootContextParameters.THIS_ENTITY, player)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT);
    return requirements.test(new LootContext.Builder(lootContext).build(Optional.empty()));
  }

  public static Builder builder() {
    return new Builder(builder -> {});
  }

  public static Builder builder(Consumer<ZombieEvents.Builder> builderFunc) {
    return new Builder(builderFunc);
  }

  public static class Builder {
    private final EquipmentTable table;
    private final ZombieEvents events;
    private RegistryKey<LootTable> onDrop = LootTableInfo.DEFAULT.onDrop();
    private int weight = 1;
    private int quality = 0;
    private List<BiomePredicate.TagSetEntry> biomePredicate = new ArrayList<>();
    private ObjectArrayList<ZombieAttributes.DefaultAttribute> defaultAttributes =
        ObjectArrayList.of();
    private ObjectArrayList<ZombieAttributes.AttributeModifier> attributeModifiers =
        ObjectArrayList.of();

    private Optional<LootCondition> lootCondition = Optional.empty();

    public Builder(EquipmentTable table, Consumer<ZombieEvents.Builder> builderFunc) {
      this.table = table;
      var builder = new ZombieEvents.Builder();
      builderFunc.accept(builder);
      this.events = builder.build();
    }

    public Builder(Consumer<ZombieEvents.Builder> builderFunc) {
      this(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT, builderFunc);
    }

    public Builder withWeight(int weight) {
      this.weight = weight;
      return this;
    }

    public Builder withQuality(int quality) {
      this.quality = quality;
      return this;
    }

    public Builder spawnIn(TagKey<Biome> tag) {
      this.biomePredicate.add(new TagSetEntry(true, tag));
      return this;
    }

    public Builder cannotSpawnIn(TagKey<Biome> tag) {
      this.biomePredicate.add(new TagSetEntry(false, tag));
      return this;
    }

    public Builder withDefaultAttributes(ZombieAttributes.DefaultAttribute... attributes) {
      for (var attribute : attributes) this.defaultAttributes.add(attribute);
      return this;
    }

    public Builder withAttributeModififers(ZombieAttributes.AttributeModifier... attributes) {
      for (var attribute : attributes) this.attributeModifiers.add(attribute);
      return this;
    }

    public Builder withLootCondition(LootCondition condition) {
      this.lootCondition = Optional.of(condition);
      return this;
    }

    public ZombieVariant build() {
      return new ZombieVariant(
          new LootTableInfo(table, onDrop),
          events,
          new BiomePredicate(List.copyOf(this.biomePredicate)),
          lootCondition,
          new ZombieAttributes(defaultAttributes, attributeModifiers),
          new SpawnWeight(weight, quality));
    }
  }
}
