package com.lothrazar.dimstack;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.lothrazar.dimstack.transit.TransitManager;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;

public class DimstackConfig {

  private static final ForgeConfigSpec.Builder CFG = new ForgeConfigSpec.Builder();
  private static ForgeConfigSpec COMMON_CONFIG;
  private static final String WALL = "####################################################################################";

  public static void setup(Path path) {
    final CommentedFileConfig configData = CommentedFileConfig.builder(path)
        .sync()
        .autosave()
        .writingMode(WritingMode.REPLACE)
        .build();
    configData.load();
    COMMON_CONFIG.setConfig(configData);
  }

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
    absoluteTransits = new String[] { "minecraft:overworld,minecraft:the_end,>,200,3,0,20,0" };
    relativeTransits = new String[] {
        //from overworld down to nether 
        //from,to,<,limit,key,ratioFLT,landing
        "minecraft:overworld,minecraft:the_nether,<,3,0,0.125,120",
        //from nether up to overworld 
        //        "minecraft:the_nether,minecraft:overworld,>,118,1,8,8",
        //end down to overworld
        //        "minecraft:the_end,minecraft:overworld,<,3,2,1,130" 
    };
    dimKeyColors.put("minecraft:overworld", 0xFF0000);
    dimPortalColors.put("minecraft:overworld", 0xFF0000);
    dimPortalUnderColors.put("minecraft:overworld", 0x0000F0);
  }

  private static void initConfig() {
    CFG.comment(WALL, "Features with configurable properties are split into categories", WALL).push(DimstackMod.MODID);
    CFG.comment(WALL, "Transit conduits", WALL)
        .push("transit");
    //
    //
    //
    //
    CFG.pop(); //transit
    CFG.pop(); //ROOT
    COMMON_CONFIG = CFG.build();
    TransitManager.reload();
  }

  public static String[] getRelativeTransits() {
    return relativeTransits;
  }

  public static String[] getAbsoluteTransits() {
    return absoluteTransits;
  }
}
