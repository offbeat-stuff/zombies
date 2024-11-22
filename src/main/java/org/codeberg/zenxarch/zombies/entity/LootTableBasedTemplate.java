package org.codeberg.zenxarch.zombies.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;
import org.codeberg.zenxarch.zombies.entity.effect.AllOfZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.DefaultZombieEffect;
import org.codeberg.zenxarch.zombies.entity.effect.ZombieEffect;

public record LootTableBasedTemplate(
    EquipmentTable equipmentTable,
    ZombieEffect onAttack,
    ZombieEffect onDeath,
    ZombieEffect onTick,
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
  public ZombieEffect onAttackEffect() {
    return onAttack;
  }

  @Override
  public int getWeight() {
    return weight;
  }

  @Override
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
    private static final Predicate<RegistryEntry<Biome>> DEFAULT_BIOME_PREDICATE = (b) -> true;
    private final List<ZombieEffect> effects;
    private final EquipmentTable table;
    private int weight = 1;
    private Predicate<RegistryEntry<Biome>> biomePredicate = DEFAULT_BIOME_PREDICATE;

    public Builder(EquipmentTable table) {
      this.effects = new ArrayList<>();
      this.table = table;
    }

    public Builder addEffect(ZombieEffect effect) {
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
      ZombieEffect effect = new DefaultZombieEffect();
      if (effects.size() == 1) effect = effects.getFirst();
      else effect = new AllOfZombieEffect(List.copyOf(effects));
      return new LootTableBasedTemplate(table, effect, effect, effect, biomePredicate, weight);
    }
  }
}
