package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.BiomePredicate.TagSetEntry;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public record ZombieTemplate(
    LootTableInfo lootTableInfo, ZombieEvents events, BiomePredicate biomePredicate, int weight) {

  public static final Codec<ZombieTemplate> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      LootTableInfo.CODEC
                          .optionalFieldOf("lootTableInfo", LootTableInfo.DEFAULT)
                          .forGetter(ZombieTemplate::lootTableInfo),
                      ZombieEvents.CODEC
                          .optionalFieldOf("events", ZombieEvents.DEFAULT)
                          .forGetter(ZombieTemplate::events),
                      BiomePredicate.CODEC
                          .optionalFieldOf("biomePredicate", BiomePredicate.DEFAULT)
                          .forGetter(ZombieTemplate::biomePredicate),
                      Codecs.NON_NEGATIVE_INT
                          .optionalFieldOf("weight", 1)
                          .forGetter(ZombieTemplate::weight))
                  .apply(instance, ZombieTemplate::new));

  public void initEquipment(
      ServerWorld world, ExtendedZombieEntity zombie, ExtendedDifficulty difficulty) {
    lootTableInfo.initEquipment(world, zombie, difficulty);
  }

  public int getWeight() {
    return weight;
  }

  public boolean canSpawnIn(RegistryEntry<Biome> biome) {
    return biomePredicate.test(biome);
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
    private List<BiomePredicate.TagSetEntry> biomePredicate = new ArrayList<>();

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

    public Builder spawnIn(TagKey<Biome> tag) {
      this.biomePredicate.add(new TagSetEntry(true, tag));
      return this;
    }

    public Builder cannotSpawnIn(TagKey<Biome> tag) {
      this.biomePredicate.add(new TagSetEntry(false, tag));
      return this;
    }

    public ZombieTemplate build() {
      return new ZombieTemplate(
          new LootTableInfo(table, onDrop),
          events,
          new BiomePredicate(List.copyOf(this.biomePredicate)),
          weight);
    }
  }
}
