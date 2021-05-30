package com.lothrazar.dimstack;

import com.lothrazar.dimstack.transit.TransitManager;
import java.util.HashMap;
import java.util.Map;
import mezz.jei.config.forge.Config;

@Config(modid = DimstackMod.MODID, category = DimstackMod.MODID + ".settings")
public class DimstackConfig {

  public static Map<String, Integer> dimPortalColors = new HashMap<>();
  public static Map<String, Integer> dimPortalUnderColors = new HashMap<>();
  //  public Int2IntMap dimKeyColors = new Int2IntOpenHashMap();
  private static Map<String, Integer> dimKeyColors = new HashMap<>();
  //  private Configuration config;
  private static String[] absoluteTransits;
  private static String[] relativeTransits;
  static {
    buildDefaults();
    initConfig();
  }

  private static void buildDefaults() {
    absoluteTransits = new String[] { "0,1,>,200,3,0,20,0" };
    relativeTransits = new String[] {
        //from overworld down to nether
        "0,-1,<,3,0,0.125,120",
        //from nether up to overworld
        "-1,0,>,118,1,8,8",
        //end down to overworld
        "1,0,<,3,2,1,130" };
    dimKeyColors.put("minecraft:overworld", 0xFF0000);
    dimPortalColors.put("minecraft:overworld", 0xFF0000);
    dimPortalUnderColors.put("minecraft:overworld", 0x0000F0);
  }

  private static void initConfig() {
    //    config.addCustomCategoryComment(cat, "Each row is one teleportation rift betewen dimensions" + "\r\nto: start dimension where item and tests are ran" + "\r\nfrom: destination" + "\r\ncompare:  < means player.y < yLimit  " + "\r\nkey: what you must hold.  Empty for no item check");
    //    absoluteTransits = //config.getStringList("TargetedTransitions", cat, 
    //        new String[] { "0,1,>,200,3,0,20,0" };//, "Simple layer transitions that target an exact location in the destination dimension.  [from,to,compare,ylimit,key meta,positionxyz]  ");
    //    relativeTransits = //config.getStringList("RelativeTransitions", cat, 
    //        new String[] {
    //            //from overworld down to nether
    //            "0,-1,<,3,0,0.125,120",
    //            //from nether up to overworld
    //            "-1,0,>,118,1,8,8",
    //            //end down to overworld
    //            "1,0,<,3,2,1,130" };//, "Transitions that are relative to player current position.  Ratio is similar to nether where x&z relative to player.  [from,to,compare,ylimit,key meta,multiplier,ylanding]");
    //    //    String[] keyColors = config.getStringList("Key Colors", "cosmetic", new String[] { "0:0xFF0000", "1:0x00FF00", "2:0x0000FF" }, "Key colors per meta.  Uses hex integers.  Format is meta:color");
    //    for (String s : keyColors) {
    //      String[] split = s.split(":");
    //      if (split.length != 2) {
    //        DimstackMod.LOGGER.error("Invalid key color entry {} will be ignored", s);
    //        continue;
    //      }
    //      int meta = Integer.parseInt(split[0]);
    //      int color = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
    //      dimKeyColors.put(meta, color);
    //    }
    //    String[] dimColors = config.getStringList("Portal Colors", "cosmetic", new String[] { "0:0xFF0000:0x0000F0", "1:0x00FF00:0xF00000", "-1:0x0000FF:0x000F00" }, "Portal colors per dimension.  Uses hex integers.  Format is dimension:bottom color:top color");
    //    for (String s : dimColors) {
    //      String[] split = s.split(":");
    //      if (split.length != 3) {
    //        DimstackMod.LOGGER.error("Invalid portal color entry {} will be ignored", s);
    //        continue;
    //      }
    //      int meta = Integer.parseInt(split[0]);
    //      int color = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
    //      int color2 = Integer.parseInt(split[2].contains("0x") ? split[2].substring(2) : split[2], split[2].contains("0x") ? 16 : 10);
    //    }
    //    config.save();
    TransitManager.reload();
  }

  public static String[] getRelativeTransits() {
    return relativeTransits;
  }

  public static String[] getAbsoluteTransits() {
    return absoluteTransits;
  }
}
