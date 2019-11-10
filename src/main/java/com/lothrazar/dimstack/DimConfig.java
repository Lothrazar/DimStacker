package com.lothrazar.dimstack;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DimstackMod.MODID, category = DimstackMod.MODID + ".settings")
public class DimConfig {

	public List<PlayerTransmit> emitters = new ArrayList<>();
	public Int2IntMap dimKeyColors = new Int2IntOpenHashMap();
	public Int2ObjectMap<int[]> dimPortalColors = new Int2ObjectOpenHashMap<>();
	private Configuration config;
	private String[] layers;
	private String[] layersRelative;
	private boolean tooltips;
	private boolean chatmessage;
	private int orangedistance;
	private int reddistance;

	public boolean doTooltips() {
		return tooltips;
	}

	public boolean doChatMessage() {
		return chatmessage;
	}

	public DimConfig(Configuration configuration) {
		this.config = configuration;
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
		this.reddistance = config.getInt("TooltipCloseDistance", cat, 8, 1, 128, "How far away from the rift will your keystone item tooltip change colour for being very close");
		this.orangedistance = config.getInt("TooltipFurtherDistance", cat, 16, 1, 128, "How far away from the rift will your keystone item tooltip change colour for being kinda close");
		cat = DimstackMod.MODID + ".layers";
		config.addCustomCategoryComment(cat, "Each row is one teleportation rift betewen dimensions" + "\r\nto: start dimension where item and tests are ran" + "\r\nfrom: destination" + "\r\ncompare:  < means player.y < yLimit  " + "\r\nkey: what you must hold.  Empty for no item check");
		this.layers = config.getStringList("TargetedTransitions", cat, new String[] { "0,1,>,200,3,0,20,0" }, "Simple layer transitions that target an exact location in the destination dimension.  [from,to,compare,ylimit,key meta,positionxyz]  ");

		this.layersRelative = config.getStringList("RelativeTransitions", cat, new String[] {
				//from overworld down to nether
				"0,-1,<,3,0,0.125,120",
				//from nether up to overworld
				"-1,0,>,118,1,8,8",
				//end down to overworld
				"1,0,<,3,2,1,130" }, "Transitions that are relative to player current position.  Ratio is similar to nether where x&z relative to player.  [from,to,compare,ylimit,key meta,multiplier,ylanding]");

		String[] keyColors = config.getStringList("Key Colors", "cosmetic", new String[] { "0:0xFF0000", "1:0x00FF00", "2:0x0000FF" }, "Key colors per meta.  Uses hex integers.  Format is meta:color");

		for (String s : keyColors) {
			String[] split = s.split(":");
			if (split.length != 2) {
				DimstackMod.logger.error("Invalid key color entry {} will be ignored", s);
				continue;
			}
			int meta = Integer.parseInt(split[0]);
			int color = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
			dimKeyColors.put(meta, color);
		}

		String[] dimColors = config.getStringList("Portal Colors", "cosmetic", new String[] { "0:0xFF0000:0x0000F0", "1:0x00FF00:0xF00000", "-1:0x0000FF:0x000F00" }, "Portal colors per dimension.  Uses hex integers.  Format is dimension:bottom color:top color");

		for (String s : dimColors) {
			String[] split = s.split(":");
			if (split.length != 3) {
				DimstackMod.logger.error("Invalid portal color entry {} will be ignored", s);
				continue;
			}

			int meta = Integer.parseInt(split[0]);
			int color = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
			int color2 = Integer.parseInt(split[2].contains("0x") ? split[2].substring(2) : split[2], split[2].contains("0x") ? 16 : 10);

			dimPortalColors.put(meta, new int[] { color, color2 });
		}

		config.save();
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
		t.keyMeta = Integer.parseInt(lrs[4]);
		if (useBlockPos) {
			int x = Integer.parseInt(lrs[5]), y = Integer.parseInt(lrs[6]), z = Integer.parseInt(lrs[7]);
			t.pos = new BlockPos(x, y, z);
			t.relative = false;
			t.ratio = 1;//ignored in this case
		} else {
			t.relative = true;
			t.ratio = Float.parseFloat(lrs[5]);
			int y = Integer.parseInt(lrs[6]);
			t.pos = new BlockPos(0, y, 0);//0's set by relative to player
		}
		return t;
	}

	public int getRedDistance() {
		return this.reddistance;
	}

	public int getOrangeDistance() {
		return this.orangedistance;
	}
}
