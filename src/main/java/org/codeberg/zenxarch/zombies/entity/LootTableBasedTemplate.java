package org.codeberg.zenxarch.zombies.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EquipmentTable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public record LootTableBasedTemplate(
    EquipmentTable equipmentTable,
    List<AttackEffect> attackEffects,
    Predicate<RegistryEntry<Biome>> biomePredicate,
    int weight)
    implements ZombieTemplate {

  @Override
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

  @Override
  public void onAttack(ServerWorld world, ExtendedZombieEntity zombie, LivingEntity attacked) {
    for (var effect : attackEffects) effect.onAttack(world, zombie, attacked);
  }

  public static Builder builder(EquipmentTable table) {
    return new Builder(table);
  }

  public static Builder builder(RegistryKey<LootTable> table, float slotDropChances) {
    return new Builder(new EquipmentTable(table, slotDropChances));
  }

  public static class Builder {
    private static final Predicate<RegistryEntry<Biome>> DEFAULT_BIOME_PREDICATE = (b) -> true;
    private final List<AttackEffect> effects;
    private final EquipmentTable table;
    private int weight = 1;
    private Predicate<RegistryEntry<Biome>> biomePredicate = DEFAULT_BIOME_PREDICATE;

    public Builder(EquipmentTable table) {
      this.effects = new ArrayList<>();
      this.table = table;
    }

    public Builder addEffect(AttackEffect effect) {
      this.effects.add(effect);
      return this;
    }

    public Builder withWeight(int weight) {
      this.weight = weight;
      return this;
    }

    public Builder spawnIn(TagKey<Biome> tag) {
      return andBiomePredicate(b -> b.isIn(tag));
    }

    public Builder cannotSpawnIn(TagKey<Biome> tag) {
      return andBiomePredicate(b -> !b.isIn(tag));
    }

    public Builder andBiomePredicate(Predicate<RegistryEntry<Biome>> predicate) {
      this.biomePredicate =
          this.biomePredicate == DEFAULT_BIOME_PREDICATE
              ? predicate
              : this.biomePredicate.and(predicate);
      return this;
    }

    public LootTableBasedTemplate build() {
      return new LootTableBasedTemplate(table, List.copyOf(effects), biomePredicate, weight);
    }
  }
}
