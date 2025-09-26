package org.codeberg.zenxarch.mob_variants_api.client;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import org.codeberg.zenxarch.mob_variants_api.MobVariantsApiMod;
import org.jetbrains.annotations.Contract;

public class ExtendedRenderStateDataKey<T> {
  private final RenderStateDataKey<T> key;
  private final Function<LivingEntity, T> provider;
  private final Predicate<T> condition;

  @Contract("_,null,_ -> fail")
  private ExtendedRenderStateDataKey(
      Identifier id, Function<LivingEntity, T> provider, Predicate<T> condition) {
    if (provider == null) throw new IllegalArgumentException("provider cannot be null");
    this.key = RenderStateDataKey.create(id::toString);
    this.provider = provider;
    this.condition = condition;
  }

  public void set(LivingEntity entity, FabricRenderState state) {
    if (provider == null) return;
    state.setData(key, provider.apply(entity));
  }

  public Optional<T> get(FabricRenderState state) {
    var value = state.getData(key);
    if (value == null) return Optional.empty();
    if (condition != null && !condition.test(value)) return Optional.empty();
    return Optional.ofNullable(value);
  }

  public static <T> Builder<T> builder(String id) {
    return new Builder<>(id);
  }

  public static <T> Builder<T> builder(Identifier id) {
    return new Builder<>(id);
  }

  public static class Builder<T> {
    private final Identifier id;
    private Function<LivingEntity, T> provider;
    private Predicate<T> condition;

    public Builder(Identifier id) {
      this.id = id;
    }

    public Builder(String id) {
      this.id = MobVariantsApiMod.id(id);
    }

    public Builder<T> attachment(AttachmentType<T> type) {
      this.provider = (living) -> living.getAttached(type);
      return this;
    }

    public <R> Builder<T> attachment(AttachmentType<R> type, Function<R, T> converter) {
      this.provider =
          (living) -> {
            var value = living.getAttached(type);
            if (value == null) return null;
            return converter.apply(value);
          };
      return this;
    }

    public Builder<T> condition(Predicate<T> condition) {
      this.condition = condition;
      return this;
    }

    public ExtendedRenderStateDataKey<T> build() {
      return new ExtendedRenderStateDataKey<>(id, provider, condition);
    }
  }
}
