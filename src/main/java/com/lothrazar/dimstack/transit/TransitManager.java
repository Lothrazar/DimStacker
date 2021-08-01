package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackConfig;
import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.util.UtilWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TransitManager {

  private static final List<Transit> TRANSITS = new ArrayList<>();

  /**
   * Gets the transmit handler for a given location.
   */
  public static Transit getTargetFor(Level world, BlockPos pos, Item key) {
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
      DimstackMod.LOGGER.error("Error: tile with null transit " + tile.getBlockPos());
      return null;
    }
    for (Transit t : TRANSITS) {
      String id = UtilWorld.dimensionToString(tile.getLevel());
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
        buildTooltip(fromLayer);
      }
    }
    for (String layer : DimstackConfig.getRelativeTransits()) {
      Transit fromLayer = Transit.fromString(layer, true);
      if (fromLayer != null) {
        TRANSITS.add(fromLayer);
        buildTooltip(fromLayer);
      }
    }
    if (TRANSITS.size() == 0) {
      DimstackMod.LOGGER.error("Error: Zero rifts detected, validate dimstack.toml config entries");
    }
  }

  static Map<ResourceLocation, List<Component>> TIPS = new HashMap<>();

  private static void buildTooltip(Transit t) {
    List<Component> strings = new ArrayList<>();
    if (t.goesUpwards) {
      // goes up so from is below
      strings.add(new TextComponent(t.to.toString()).withStyle(ChatFormatting.LIGHT_PURPLE));
      strings.add(new TranslatableComponent("dimstack.riftkey." + (t.goesUpwards ? "up" : "down")).withStyle(ChatFormatting.GRAY));
      strings.add(new TextComponent(t.from.toString()).withStyle(ChatFormatting.DARK_PURPLE));
    }
    else { // down
      strings.add(new TextComponent(t.from.toString()).withStyle(ChatFormatting.DARK_PURPLE));
      strings.add(new TranslatableComponent("dimstack.riftkey." + (t.goesUpwards ? "up" : "down")).withStyle(ChatFormatting.GRAY));
      strings.add(new TextComponent(t.to.toString()).withStyle(ChatFormatting.LIGHT_PURPLE));
    }
    TIPS.put(t.itemId, strings);
  }

  public static List<Component> getTooltip(ItemStack itemStack) {
    ResourceLocation id = itemStack.getItem().getRegistryName();
    List<Component> tts = new ArrayList<>();
    if (TIPS.containsKey(id)) {
      tts.addAll(TIPS.get(id));
    }
    return tts;
  }
}
