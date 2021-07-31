package com.lothrazar.dimstack.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;

public class UtilWorld {

  public static String dimensionToString(Level world) {
    //example: returns "minecraft:overworld" resource location
    return world.dimension().location().toString();
  }

  // new ResourceLocation("twilightforest", "twilightforest")
  public static ResourceKey<Level> stringToDimension(ResourceLocation key) {
    return ResourceKey.create(Registry.DIMENSION_REGISTRY, key);
  }
}
