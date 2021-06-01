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
    TransitManager.reload();
  }

  public static Map<String, Integer> DIMPORTALCOLORS = new HashMap<>();
  public static Map<String, Integer> DIMKEYCOLORS = new HashMap<>();
  private static String[] ABSOLUTETRANSITS;
  private static String[] RELATIVETRANSITS;
  static {
    buildDefaults();
    initConfig();
  }

  private static void buildDefaults() {
    ABSOLUTETRANSITS = new String[] {
        //from,to,<,limit,x,y,z   
        "minecraft:overworld,minecraft:the_end,>,200,0,20,0" };
    RELATIVETRANSITS = new String[] {
        //from,to,<,limit,ratioFLT,landing
        //from overworld down to nether 
        "minecraft:overworld,minecraft:the_nether,<,8,0.125,120",
        //from nether up to overworld 
        "minecraft:the_nether,minecraft:overworld,>,118,8,8",
        //end down to overworld
        "minecraft:the_end,minecraft:overworld,<,3,1,130",
        //from nether down to twilight
        "minecraft:the_nether,twilightforest:twilightforest,<,8,1,220",
        //from twilight up to nether 
        "twilightforest:twilightforest,minecraft:the_nether,>,218,1,8",
        //
        //same but overworld and twilight to test
        //from nether down to twilight
        "minecraft:overworld,twilightforest:twilightforest,<,8,1,220",
        //from twilight up to nether 
        "twilightforest:twilightforest,minecraft:overworld,>,218,1,8",
        //
        //from nether down to u
        "minecraft:the_nether,undergarden:undergarden,<,8,1,220",
        //from u up to nether 
        "undergarden:undergarden,minecraft:the_nether,>,218,1,8",
        //
        //from nether down to g
        "minecraft:the_nether,gaiadimension:gaia_dimension,<,8,1,220",
        //from g up to nether 
        "gaiadimension:gaia_dimension,minecraft:the_nether,>,218,1,8",
        //
        //from nether down to lc
        "minecraft:the_nether,lostcities:lostcity,<,8,1,220",
        //from lc up to nether 
        "lostcities:lostcity,minecraft:the_nether,>,218,1,8"
    };
    DIMKEYCOLORS.put("minecraft:overworld", 0xFF0000);
    DIMKEYCOLORS.put("minecraft:the_nether", 0x00FF00);
    DIMKEYCOLORS.put("minecraft:the_end", 0x7C009C);
    DIMKEYCOLORS.put("twilightforest:twilightforest", 0x000ba5);
    DIMKEYCOLORS.put("undergarden:undergarden", 0x808080);
    DIMKEYCOLORS.put("gaiadimension:gaia_dimension", 0x008000);
    DIMKEYCOLORS.put("lostcities:lostcity", 0x800000);
    DIMPORTALCOLORS.put("minecraft:overworld", 0x00FF00);
    DIMPORTALCOLORS.put("minecraft:the_nether", 0xFF0000);
    DIMPORTALCOLORS.put("minecraft:the_end", 0x7C009C);
    DIMPORTALCOLORS.put("twilightforest:twilightforest", 0x000ba5);
    DIMPORTALCOLORS.put("undergarden:undergarden", 0x808080);
    DIMPORTALCOLORS.put("gaiadimension:gaia_dimension", 0x008000);
    DIMPORTALCOLORS.put("lostcities:lostcity", 0x800000);
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
  }

  public static String[] getRelativeTransits() {
    return RELATIVETRANSITS;
  }

  public static String[] getAbsoluteTransits() {
    return ABSOLUTETRANSITS;
  }
}
