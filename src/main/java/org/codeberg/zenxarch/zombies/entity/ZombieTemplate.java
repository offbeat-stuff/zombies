package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
import org.codeberg.zenxarch.zombies.entity.effect.AllOfZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.DefaultZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;
import org.codeberg.zenxarch.zombies.loot_table.ZombieLootTables;

public record ZombieTemplate(
    LootTableInfo lootTableInfo,
    ZombieEffect onAttack,
    ZombieEffect onDeath,
    ZombieEffect onTick,
    BiomePredicate biomePredicate,
    int weight) {

  private static RecordCodecBuilder<ZombieTemplate, ZombieEffect> effect(
      String name, Function<ZombieTemplate, ZombieEffect> getter) {
    return ZombieEffect.CODEC.optionalFieldOf(name, DefaultZombieEffect.create()).forGetter(getter);
  }

  public static final Codec<ZombieTemplate> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      LootTableInfo.CODEC
                          .optionalFieldOf("lootTableInfo", LootTableInfo.DEFAULT)
                          .forGetter(ZombieTemplate::lootTableInfo),
                      effect("onAttack", ZombieTemplate::onAttack),
                      effect("onDeath", ZombieTemplate::onDeath),
                      effect("onTick", ZombieTemplate::onTick),
                      BiomePredicate.CODEC
                          .optionalFieldOf("biomePredicate", BiomePredicate.create())
                          .forGetter(ZombieTemplate::biomePredicate),
                      Codecs.NON_NEGATIVE_INT
                          .optionalFieldOf("weight", 1)
                          .forGetter(ZombieTemplate::weight))
                  .apply(instance, ZombieTemplate::new));

  public void initEquipment(
      ServerWorld world, ExtendedZombieEntity zombie, ExtendedDifficulty difficulty) {
    lootTableInfo.initEquipment(world, zombie, difficulty);
  }

  public ZombieEffect onAttackEffect() {
    return onAttack;
  }

  public ZombieEffect onKillEffect() {
    return onDeath;
  }

  public ZombieEffect onTickEffect() {
    return onTick;
  }

  public int getWeight() {
    return weight;
  }

  public boolean canSpawnIn(RegistryEntry<Biome> biome) {
    return biomePredicate.test(biome);
  }

  public static Builder builder(EquipmentTable table) {
    return new Builder(table);
  }

  public static Builder builder(RegistryKey<LootTable> table, float slotDropChances) {
    return new Builder(new EquipmentTable(table, slotDropChances));
  }

  public static class Builder {
    private ZombieEffect onAttack;
    private ZombieEffect onDeath;
    private ZombieEffect onTick;
    private final EquipmentTable table;
    private RegistryKey<LootTable> onDrop = LootTableInfo.DEFAULT.onDrop();
    private int weight = 1;
    private List<BiomePredicate.TagSetEntry> biomePredicate = new ArrayList<>();

    public Builder(EquipmentTable table) {
      this.onAttack = DefaultZombieEffect.create();
      this.onDeath = DefaultZombieEffect.create();
      this.onTick = DefaultZombieEffect.create();
      this.table = table;
    }

    public Builder() {
      this(ZombieLootTables.COMMON_ZOMBIE_EQUIPMENT);
    }

    public Builder withOnAttack(ZombieEffect... effects) {
      this.onAttack = AllOfZombieEffect.create(effects);
      return this;
    }

    public Builder withOnDeath(ZombieEffect... effects) {
      this.onDeath = AllOfZombieEffect.create(effects);
      return this;
    }

    public Builder withOnTick(ZombieEffect... effects) {
      this.onTick = AllOfZombieEffect.create(effects);
      return this;
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
          onAttack,
          onDeath,
          onTick,
          new BiomePredicate(List.copyOf(this.biomePredicate)),
          weight);
    }
  }
}
