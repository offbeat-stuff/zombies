package org.codeberg.zenxarch.zombies.loot_table;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;

public record TimeCheckLootCondition(Optional<Boolean> isDay, Optional<Boolean> isNight)
    implements LootCondition {

  public static final MapCodec<TimeCheckLootCondition> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      Codec.BOOL.optionalFieldOf("day").forGetter(TimeCheckLootCondition::isDay),
                      Codec.BOOL
                          .optionalFieldOf("night")
                          .forGetter(TimeCheckLootCondition::isNight))
                  .apply(instance, TimeCheckLootCondition::new));

  @Override
  public boolean test(LootContext context) {
    var world = context.getWorld();
    return isDay.map(v -> v == world.isDay()).orElse(true)
        && isNight.map(v -> v == world.isNight()).orElse(true);
  }

  @Override
  public LootConditionType getType() {
    return ZombieLootConditionTypes.TIME_CHECK;
  }

  public static org.codeberg.zenxarch.zombies.loot_table.TimeCheckLootCondition.Builder create() {
    return new org.codeberg.zenxarch.zombies.loot_table.TimeCheckLootCondition.Builder();
  }

  public static class Builder implements LootCondition.Builder {
    private Optional<Boolean> day = Optional.empty();
    private Optional<Boolean> night = Optional.empty();

    public org.codeberg.zenxarch.zombies.loot_table.TimeCheckLootCondition.Builder day(boolean day) {
      this.day = Optional.of(day);
      return this;
    }

    public org.codeberg.zenxarch.zombies.loot_table.TimeCheckLootCondition.Builder night(boolean night) {
      this.night = Optional.of(night);
      return this;
    }

    public TimeCheckLootCondition build() {
      return new TimeCheckLootCondition(this.day, this.night);
    }
  }
}
