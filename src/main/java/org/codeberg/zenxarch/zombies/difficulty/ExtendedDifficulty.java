package org.codeberg.zenxarch.zombies.difficulty;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.codeberg.zenxarch.zombies.random.RandomUtils;
import org.jetbrains.annotations.Unmodifiable;

@Unmodifiable
public class ExtendedDifficulty extends LocalDifficulty {

  private static final Random random = Random.create();

  private final double difficulty;

  public ExtendedDifficulty(ServerWorld world, BlockPos pos) {
    super(world.getDifficulty(), 0, 0, 0);
    this.difficulty = DifficultyCalculations.calculateDifficulty(world, pos);
  }

  @Override
  public float getLocalDifficulty() {
    return (float) (this.difficulty * 6.75);
  }

  @Override
  public boolean isAtLeastHard() {
    return getLocalDifficulty() >= (float) Difficulty.HARD.ordinal();
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
    return (int)
        MathHelper.clampedLerp(25.0, 50.0 * this.getGlobalDifficulty().ordinal(), difficulty);
  }

  public boolean shouldEnchantEquipment() {
    return RandomUtils.nextBoolean(random, MathHelper.lerp(this.difficulty, -0.5, 0.5));
  }

  public boolean shouldSpawnWithEquipment(EquipmentSlot slot) {
    return switch (slot) {
      case MAINHAND, OFFHAND -> RandomUtils.nextBoolean(random, this.difficulty);
      default -> RandomUtils.nextBoolean(random, this.difficulty, 4);
    };
  }

  public boolean isDisabled() {
    return this.difficulty <= 0.0;
  }
}
