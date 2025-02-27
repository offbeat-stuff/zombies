package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.codeberg.zenxarch.zombies.ZombieGamerules;
import org.codeberg.zenxarch.zombies.math.RandomRange;
import org.codeberg.zenxarch.zombies.math.RandomUtils;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class ExtendedDifficulty extends LocalDifficulty {

  private static final Random random = Random.create();

  private final double difficulty;
  private final int maxZombies;

  public ExtendedDifficulty(ServerWorld world, double difficulty, int maxZombies) {
    super(world.getDifficulty(), 0, 0, 0);
    this.difficulty = difficulty;
    this.maxZombies = maxZombies;
  }

  public ExtendedDifficulty(ServerWorld world, BlockPos pos) {
    super(world.getDifficulty(), 0, 0, 0);
    this.difficulty = DifficultyCalculations.calculateDifficulty(world, pos);
    final var maxPossibleZombies = world.getGameRules().getInt(ZombieGamerules.MAX_ZOMBIES);
    this.maxZombies =
        this.difficulty >= 1.0 ? maxPossibleZombies : (int) (this.difficulty * maxPossibleZombies);
  }

  @Override
  public float getLocalDifficulty() {
    return (float) (this.difficulty * 6.75);
  }

  @Override
  public boolean isAtLeastHard() {
    return getLocalDifficulty() >= Difficulty.HARD.ordinal();
  }

  @Override
  public boolean isHarderThan(float difficulty) {
    return getLocalDifficulty() > difficulty;
  }

  @Override
  public float getClampedLocalDifficulty() {
    return (float) this.difficulty;
  }

  public int getMaxZombies() {
    return this.maxZombies;
  }

  private static final RandomRange shouldEnchant = new RandomRange(-0.5, 0.5);

  public boolean shouldEnchantEquipment() {
    return shouldEnchant.nextBoolean(random, difficulty);
  }

  public boolean shouldSpawnWithEquipment(EquipmentSlot slot) {
    return switch (slot) {
      case MAINHAND, OFFHAND -> RandomUtils.nextBoolean(random, this.difficulty);
      default -> RandomUtils.nextBoolean(random, this.difficulty, 4);
    };
  }
}
