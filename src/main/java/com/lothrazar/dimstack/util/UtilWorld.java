package com.lothrazar.dimstack.util;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class UtilWorld {

  public static String dimensionToString(World world) {
    //example: returns "minecraft:overworld" resource location
    return world.getDimensionKey().getLocation().toString();
  }

  // new ResourceLocation("twilightforest", "twilightforest")
  public static RegistryKey<World> stringToDimension(ResourceLocation key) {
    return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, key);
  }
}
