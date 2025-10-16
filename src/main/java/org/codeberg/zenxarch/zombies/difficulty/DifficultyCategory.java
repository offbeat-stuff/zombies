package org.codeberg.zenxarch.zombies.difficulty;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import org.codeberg.zenxarch.zombies.Zombies;
import org.codeberg.zenxarch.zombies.difficulty.entry.DifficultyEntry;

public final class DifficultyCategory {
  private DifficultyCategory() {
    throw new IllegalStateException("Only static members");
  }

  private static final Map<Identifier, List<DifficultyEntry>> categories =
      new Object2ObjectOpenHashMap<>();

  public static void addDifficultyEntry(Identifier id, DifficultyEntry entry) {
    categories.computeIfAbsent(id, unused -> new ObjectArrayList<>()).add(entry);
  }

  public static final AttachmentType<CachedValue> DIFFICULTY =
      CachedValue.createAttachmentType(Zombies.id("difficulty"));

  public static double calculateDifficulty(ServerWorld world, Chunk chunk) {
    if (chunk == null) return 1.0;
    return CachedValue.getOrUpdateValue(
        world, chunk, DIFFICULTY, DifficultyCategory::calculateDifficultyWOCache);
  }

  private static double calculateDifficultyWOCache(ServerWorld world, Chunk chunk) {
    var result = 1.0;
    for (var category : categories.values()) result *= calculateDifficulty(world, chunk, category);
    return result;
  }

  private static double calculateDifficulty(
      ServerWorld world, Chunk chunk, List<DifficultyEntry> entries) {
    var calculations =
        entries.stream().map(entry -> new DifficultyCalculation(entry, world, chunk)).toList();
    final var weightSum = calculations.stream().mapToInt(DifficultyCalculation::weight).sum();
    return calculations.stream()
        .mapToDouble(entry -> (entry.value * entry.weight) / weightSum)
        .sum();
  }

  public static Set<Identifier> getCategories() {
    return categories.keySet();
  }

  public static double calculateDifficulty(ServerWorld world, Chunk chunk, Identifier id) {
    return calculateDifficulty(world, chunk, categories.get(id));
  }

  private static record DifficultyCalculation(double value, int weight) {
    public DifficultyCalculation(DifficultyEntry entry, ServerWorld world, Chunk chunk) {
      this(MathHelper.clamp(entry.calculate(world, chunk), 0.0, 1.0), entry.getWeight());
    }
  }
}
