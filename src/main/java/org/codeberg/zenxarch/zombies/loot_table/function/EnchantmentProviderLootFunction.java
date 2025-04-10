package org.codeberg.zenxarch.zombies.loot_table.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import org.codeberg.zenxarch.zombies.difficulty.ExtendedDifficulty;

public record EnchantmentProviderLootFunction(RegistryKey<EnchantmentProvider> provider)
    implements LootFunction {

  public static final MapCodec<EnchantmentProviderLootFunction> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      RegistryKey.createCodec(RegistryKeys.ENCHANTMENT_PROVIDER)
                          .fieldOf("provider")
                          .forGetter(EnchantmentProviderLootFunction::provider))
                  .apply(instance, EnchantmentProviderLootFunction::new));

  @Override
  public ItemStack apply(ItemStack stack, LootContext context) {
    EnchantmentHelper.applyEnchantmentProvider(
        stack,
        context.getWorld().getRegistryManager(),
        provider,
        new ExtendedDifficulty(context.getWorld(), context.getLuck(), 0),
        context.getRandom());
    return stack;
  }

  @Override
  public LootFunctionType<? extends LootFunction> getType() {
    return ZombieLootFunctionTypes.ENCHANTMENT_PROVIDER;
  }
}
