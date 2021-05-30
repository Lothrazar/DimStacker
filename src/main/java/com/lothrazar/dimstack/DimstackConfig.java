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

  public static Map<String, Integer> DIMPORTALCOLORS = new HashMap<>();
  //  public static Map<String, Integer> DIMPORTALUNDERCOLORS = new HashMap<>();
  //  public Int2IntMap dimKeyColors = new Int2IntOpenHashMap();
  public static Map<String, Integer> DIMKEYCOLORS = new HashMap<>();
  //  private Configuration config;
  private static String[] ABSOLUTETRANSITS;
  private static String[] RELATIVETRANSITS;
  static {
    buildDefaults();
    initConfig();
  }

  private static void buildDefaults() {
    ABSOLUTETRANSITS = new String[] { "minecraft:overworld,minecraft:the_end,>,200,0,20,0" };
    RELATIVETRANSITS = new String[] {
        //from overworld down to nether 
        //from,to,<,limit,key,ratioFLT,landing
        "minecraft:overworld,minecraft:the_nether,<,3,0.125,120",
        //from nether up to overworld 
        "minecraft:the_nether,minecraft:overworld,>,118,8,8",
        //end down to overworld
        "minecraft:the_end,minecraft:overworld,<,3,1,130"
    };
    DIMKEYCOLORS.put("minecraft:overworld", 0xFF0000);
    DIMKEYCOLORS.put("minecraft:the_nether", 0x00FF00);
    DIMKEYCOLORS.put("minecraft:the_end", 0x7C009C);
    DIMPORTALCOLORS.put("minecraft:overworld", 0xFF0000);
    DIMPORTALCOLORS.put("minecraft:the_nether", 0x00FF00);
    DIMPORTALCOLORS.put("minecraft:the_end", 0x7C009C);
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
    return RELATIVETRANSITS;
  }

  public static String[] getAbsoluteTransits() {
    return ABSOLUTETRANSITS;
  }
}
