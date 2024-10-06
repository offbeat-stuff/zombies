package org.codeberg.zenxarch.zombies.config;

import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedNameConvention;
import folk.sisby.kaleido.lib.quiltconfig.api.metadata.NamingSchemes;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;

@SerializedNameConvention(NamingSchemes.SNAKE_CASE)
public class DebugConfig extends ReflectiveConfig {
  @Comment("If enabled spawning stats are logged every 5 minutes")
  public final TrackedValue<Boolean> LOGGING = this.value(false);

  @Comment("If enabled partciles for shown for mod spawned zombies")
  public final TrackedValue<Boolean> PARTICLES = this.value(false);
}
