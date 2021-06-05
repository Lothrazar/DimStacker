package com.lothrazar.dimstack;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class DimstackConfig {

  private static final ForgeConfigSpec.Builder CFG = new ForgeConfigSpec.Builder();
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

  private static ForgeConfigSpec COMMON_CONFIG;
  public static Map<String, Integer> DIMPORTALCOLORS = new HashMap<>();
  public static Map<String, Integer> DIMKEYCOLORS = new HashMap<>();
  private static String[] ABSOLUTETRANSITS;
  private static String[] RELATIVETRANSITS;
  private static ConfigValue<List<? extends String>> REL;
  private static ConfigValue<List<? extends String>> ABS;
  static {
    buildDefaults();
    initConfig();
  }

  private static void buildDefaults() {
    ABSOLUTETRANSITS = new String[] {
        //from,to,<,limit,x,y,z   
        "dimstack:end_key,minecraft:overworld,minecraft:the_end,>,200,0,20,0" };
    RELATIVETRANSITS = new String[] {
        //from,to,<,limit,ratio,landing
        //from overworld down to nether 
        "dimstack:nether_key,minecraft:overworld,minecraft:the_nether,<,8,0.125,120",
        //from nether up to overworld 
        "dimstack:overworld_key,minecraft:the_nether,minecraft:overworld,>,118,8,8",
        //end down to overworld
        "dimstack:overworld_key,minecraft:the_end,minecraft:overworld,<,3,1,130",
        //from nether down to twilight
        "dimstack:twilight_key,minecraft:the_nether,twilightforest:twilightforest,<,8,1,220",
        //from twilight up to ow 
        "dimstack:overworld_key,twilightforest:twilightforest,minecraft:the_nether,>,218,1,8",
        //
        //same but overworld and twilight to test
        //from nether down to twilight
        "dimstack:twilight_key,minecraft:overworld,twilightforest:twilightforest,<,8,1,220",
        //from twilight up to OW 
        "dimstack:overworld_key,twilightforest:twilightforest,minecraft:overworld,>,218,1,8",
        //
        //from nether down to u
        "dimstack:undergarden_key,minecraft:the_nether,undergarden:undergarden,<,8,1,220",
        //from u up to nether 
        "dimstack:nether_key,undergarden:undergarden,minecraft:the_nether,>,218,1,8",
        //
        //from nether down to g
        "dimstack:gaia_key,minecraft:the_nether,gaiadimension:gaia_dimension,<,8,1,220",
        //from g up to nether 
        "dimstack:nether_key,gaiadimension:gaia_dimension,minecraft:the_nether,>,218,1,8",
        //
        //from nether down to lc
        "dimstack:lostcities_key,minecraft:the_nether,lostcities:lostcity,<,8,1,220",
        //from lc up to nether 
        "dimstack:nether_key,lostcities:lostcity,minecraft:the_nether,>,218,1,8"
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
    CFG.comment(WALL, "All dimensional rifts are defined here. \r\n"
        + "Each rift entry is a CSV string with very precise strict values, bad data will crash the game\r\n"
        + "If you get a crash, read the logs, backup/delete the config and try again\r\n"
        + "Read the comments, all values are documented ", WALL).push(DimstackMod.MODID);
    //
    //
    //
    CFG.comment(WALL, "Transit conduits", WALL)
        .push("transit");
    //
    REL = CFG.comment("\r\nRelative rifts, link similar locations in dimensions. format must be EXACT\r\n"
        + " Also important: Rifts are only one way.  So you almost always want TWO entries in this list, for example linking nether and overworld has two rifts\r\n"
        + " Example: The default config says from the overworld, to the nether, go DOWN below limit 8 and use the key, then you have a landing of 120 y value\r\n"
        + "         item,from,to,<,limit,ratio,landing\r\n"
        + " from is the dimension id where you use the key to create the portal\r\n"
        + " to is the dimension id you end up on the other side of the rift\r\n"
        + " < means rift is at the bottom of the dimension below limit, > means rift is at the TOP of the dimension above limit\r\n"
        + " limit is the y value that must be reached to find the rift. example if you have '<30' then y smaller than 30 means rift can be activated\r\n"
        + " ratio explains how the nether is an 8:1 coords size difference, most dimensions are just 1 here\r\n"
        + " landing is where the other end of the portal takes you, normally if limit is small, then landing is large to end up at the top of the target dimension\r\n"
        + "").defineList("relative", Arrays.asList(RELATIVETRANSITS),
            it -> it instanceof String);
    ABS = CFG.comment("\r\nAbsolute rifts.  Like how going to the end always puts you on the center island\r\n"
        + "         item,from,to,<,limit,x,y,z  \r\n"
        + " from is the dimension id where you use the key to create the portal\r\n"
        + " to is the dimension id you end up on the other side of the rift\r\n"
        + " < means rift is at the bottom of the dimension below limit, > means rift is at the TOP of the dimension above limit\r\n"
        + " limit is the y value that must be reached to find the rift. example if you have '<30' then y smaller than 30 means rift can be activated\r\n"
        + " Now just put the x,y,z values where the rift takes you in the to dimension\r\n"
        + "").defineList("absolute", Arrays.asList(ABSOLUTETRANSITS),
            it -> it instanceof String);
    //
    CFG.pop(); //transit
    CFG.pop(); //ROOT
    COMMON_CONFIG = CFG.build();
  }

  public static String[] getRelativeTransits() {
    return REL.get().toArray(new String[0]);
  }

  public static String[] getAbsoluteTransits() {
    return ABS.get().toArray(new String[0]);
  }
}
