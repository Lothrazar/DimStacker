package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackConfig;
import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.util.UtilWorld;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransitManager {

  private static final List<Transit> TRANSITS = new ArrayList<>();

  /**
   * Gets the transmit handler for a given location.
   */
  public static Transit getTargetFor(World world, BlockPos pos, Item key) {
    for (Transit t : TRANSITS) {
      int playerY = pos.getY();
      String id = UtilWorld.dimensionToString(world);
      //      int id = world.getDimension().getType().getId();
      //if i am in source dim for this
      //and my target dim matches the key 
      //      DimstackMod.LOGGER.info("test " + key.targetDimension
      //          + "TARGET " + t.getTargetDim().toString().equalsIgnoreCase(key.targetDimension));
      if (t.getSourceDim().toString().equalsIgnoreCase(id) &&
      // t.getTargetDim().toString().equalsIgnoreCase(key.targetDimension)
          t.itemId.toString().equalsIgnoreCase(key.getRegistryName().toString())) {
        if (t.goesUpwards() && playerY > t.yLimit) {
          return t;
        }
        else if (!t.goesUpwards() && playerY <= t.yLimit) {
          return t;
        }
      }
    }
    return null;
  }

  /**
   * Gets the transmit handler for a given tile.
   */
  public static Transit getTargetFor(PortalTile tile) {
    if (tile.getTransit() == null) {
      DimstackMod.LOGGER.error("Error: tile with null transit " + tile.getPos());
      return null;
    }
    for (Transit t : TRANSITS) {
      String id = UtilWorld.dimensionToString(tile.getWorld());
      // also tranit and tile must target the same place
      ResourceLocation loopTargetDim = t.getTargetDim();
      ResourceLocation tileTargetDim = tile.getTransit().getTargetDim();
      if (loopTargetDim.toString().equalsIgnoreCase(tileTargetDim.toString())
          &&
          t.getSourceDim().toString().equalsIgnoreCase(id)) {
        if (t.goesUpwards() == tile.goesUpwards()) {
          return t;
        }
      }
    }
    return null;
  }

  public static List<Transit> getAllTransits() {
    return TRANSITS;
  }

  public static void reload() {
    TRANSITS.clear();
    for (String layer : DimstackConfig.getAbsoluteTransits()) {
      Transit fromLayer = Transit.fromString(layer, false);
      if (fromLayer != null) {
        TRANSITS.add(fromLayer);
      }
    }
    for (String layer : DimstackConfig.getRelativeTransits()) {
      Transit fromLayer = Transit.fromString(layer, true);
      if (fromLayer != null) {
        TRANSITS.add(fromLayer);
      }
    }
    if (TRANSITS.size() == 0) {
      DimstackMod.LOGGER.error("Error: Zero rifts detected, validate dimstack.toml config entries");
    }
  }
}
