package org.codeberg.zenxarch.zombies.helper;

import java.util.List;
import java.util.function.Function;
import net.minecraft.item.Item;
import net.minecraft.util.math.random.Random;

@FunctionalInterface
public interface ItemSelector {
  public int apply(List<Item> items, Random random, double difficulty);

  public static record WeightedSelector(
      Function<Double, Double> minSupplier, Function<Double, Double> maxSupplier)
      implements ItemSelector {

    @Override
    public int apply(List<Item> items, Random random, double difficulty) {
      return LerpImpl.lerpWeighted(
          random, minSupplier.apply(difficulty), maxSupplier.apply(difficulty), items.size());
    }
  }

  public static record RecursiveChanceBased(Function<Double, Double> chanceSupplier)
      implements ItemSelector {

    @Override
    public int apply(List<Item> items, Random random, double difficulty) {
      if (items.isEmpty()) return -1;
      var chance = chanceSupplier.apply(difficulty);
      for (int i = 0; i < items.size(); i++)
        if (ProbabilityImpl.nextBoolean(random, chance)) return i;
      return 0;
    }
  }
}
