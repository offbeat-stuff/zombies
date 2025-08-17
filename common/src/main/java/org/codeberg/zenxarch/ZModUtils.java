package org.codeberg.zenxarch;

import net.minecraft.util.Identifier;

public class ZModUtils {
  public static Identifier id(String path) {
    return Identifier.of("zenxarch", path);
  }
}
