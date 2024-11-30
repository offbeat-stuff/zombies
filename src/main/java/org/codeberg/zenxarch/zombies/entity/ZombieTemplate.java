package org.codeberg.zenxarch.zombies.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.BiomePredicate.TagSetEntry;
import org.codeberg.zenxarch.zombies.entity.effect.AllOfZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.DefaultZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;

public record ZombieTemplate(
    EquipmentTable equipmentTable,
    ZombieEffect onAttack,
    ZombieEffect onDeath,
    ZombieEffect onTick,
    BiomePredicate biomePredicate,
    int weight) {

  public static final Codec<ZombieTemplate> CODEC =
      RecordCodecBuilder.create(
          instance ->
              instance
                  .group(
                      EquipmentTable.CODEC
                          .fieldOf("equipmentTable")
                          .forGetter(ZombieTemplate::equipmentTable),
                      ZombieEffect.CODEC.fieldOf("onAttack").forGetter(ZombieTemplate::onAttack),
                      ZombieEffect.CODEC.fieldOf("onDeath").forGetter(ZombieTemplate::onDeath),
                      ZombieEffect.CODEC.fieldOf("onTick").forGetter(ZombieTemplate::onTick),
                      BiomePredicate.CODEC
                          .fieldOf("biomePredicate")
                          .forGetter(ZombieTemplate::biomePredicate),
                      Codecs.NON_NEGATIVE_INT.fieldOf("weight").forGetter(ZombieTemplate::weight))
                  .apply(instance, ZombieTemplate::new));

  public void initEquipment(
      ServerWorld world, ZombieEntity zombie, ExtendedDifficulty difficulty, Random random) {
    zombie.setEquipmentFromTable(
        equipmentTable.lootTable(),
        new LootWorldContext.Builder(world)
            .add(LootContextParameters.ORIGIN, zombie.getPos())
            .add(LootContextParameters.THIS_ENTITY, zombie)
            .luck(difficulty.getClampedLocalDifficulty())
            .build(LootContextTypes.EQUIPMENT),
        equipmentTable.slotDropChances());
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
    private int weight = 1;
    private List<BiomePredicate.TagSetEntry> biomePredicate = new ArrayList<>();

    public Builder(EquipmentTable table) {
      this.onAttack = DefaultZombieEffect.create();
      this.onDeath = DefaultZombieEffect.create();
      this.onTick = DefaultZombieEffect.create();
      this.table = table;
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
          table,
          onAttack,
          onDeath,
          onTick,
          new BiomePredicate(List.copyOf(this.biomePredicate)),
          weight);
    }
  }
}
