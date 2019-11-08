package com.lothrazar.dimstack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DimstackMod.MODID, category = DimstackMod.MODID + ".settings")
public class DimConfig {

  public List<PlayerTransmit> emitters = new ArrayList<>();
  private Configuration config;
  private String[] layers;
  private String[] layersRelative;
  private boolean tooltips;
  private boolean chatmessage;

  public boolean doTooltips() {
    return tooltips;
  }

  public boolean doChatMessage() {
    return chatmessage;
  }

  public DimConfig(Configuration configuration) {
    this.config = configuration;
    config.load();
    syncConfig();
  }

  @SubscribeEvent
  public void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
    if (event.getModID().equals(DimstackMod.MODID)) {
      syncConfig();
    }
  }

  private void syncConfig() {
    String cat = DimstackMod.MODID + ".extras";
    this.tooltips = config.getBoolean("ShowTooltips", cat, true, "Show tooltips on dimensional keys");
    this.chatmessage = config.getBoolean("ShowChatOnTeleport", cat, true, "Show chat message on rift use");
    cat = DimstackMod.MODID + ".layers";
    config.addCustomCategoryComment(cat, "Each row is one teleportation rift betewen dimensions"
        + "\r\nto: start dimension where item and tests are ran"
        + "\r\nfrom: destination"
        + "\r\ncompare:  < means player.y < yLimit  "
        + "\r\nkey: what you must hold.  Empty for no item check");
    this.layers = config.getStringList("TargetedTransitions", cat, new String[] {
        //overworld up to end
        "0,1,>,200,minecraft:ender_eye,0,20,0"
    },
        "Simple layer transitions that target an exact location in the destination dimension.  [from,to,compare,ylimit,key,positionxyz]  ");
    //////////////////////
    this.layersRelative = config.getStringList("RelativeTransitions", cat, new String[] {
        //from overworld down to nether
        "0,-1,<,3,minecraft:obsidian,0.125,120",
        //from nether up to overworld
        "-1,0,>,118,minecraft:dirt,8,8",
        //end down to overworld
        "1,0,<,3,minecraft:wool:3,1,130" },
        "Transitions that are relative to player current position.  Ratio is similar to nether where x&z relative to player.  [from,to,compare,ylimit,key,multiplier,ylanding]"
    //            + "\r\n"
    );
    //    if (config.hasChanged()) {
    config.save();
    //    } 
    this.parseEmitters();
  }

  private void parseEmitters() {
    this.emitters = new ArrayList<>();
    for (String layer : this.layers) {
      this.emitters.add(this.parseLayer(layer, true));
    }
    for (String layer : this.layersRelative) {
      this.emitters.add(this.parseLayer(layer, false));
    }
  }

  private PlayerTransmit parseLayer(String layer, boolean useBlockPos) {
    DimstackMod.logger.info("config parsing :" + layer);
    String[] lrs = layer.split(",");
    PlayerTransmit t = new PlayerTransmit();
    t.from = Integer.parseInt(lrs[0]);
    t.to = Integer.parseInt(lrs[1]);
    t.greaterThan = ">".equalsIgnoreCase(lrs[2]);
    t.yLimit = Integer.parseInt(lrs[3]);
    t.key = lrs[4];
    if (t.key == null || t.key.isEmpty() || t.key.equalsIgnoreCase("null")) {
      t.key = null;
    }
    else {
      String[] tkns = t.key.split(":");
      int test = tkns.length;
      t.keyMeta = 0;
      if (test >= 3) {
        t.keyMeta = Integer.parseInt(t.key.split(":")[2]);
        t.key = tkns[0] + ":" + tkns[1];
      }
    }
    if (useBlockPos) {
      int x = Integer.parseInt(lrs[5]),
          y = Integer.parseInt(lrs[6]),
          z = Integer.parseInt(lrs[7]);
      t.pos = new BlockPos(x, y, z);
      t.relative = false;
      t.ratio = 1;//ignored in this case
    }
    else {
      t.relative = true;
      t.ratio = Float.parseFloat(lrs[5]);
      int y = Integer.parseInt(lrs[6]);
      t.pos = new BlockPos(0, y, 0);//0's set by relative to player
    }
    return t;
  }
}
