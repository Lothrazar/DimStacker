package com.lothrazar.dimstack;

import com.lothrazar.dimstack.transit.TransitManager;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = DimstackMod.MODID, category = DimstackMod.MODID + ".settings")
public class DimConfig {

	public Int2IntMap dimKeyColors = new Int2IntOpenHashMap();
	public Int2ObjectMap<int[]> dimPortalColors = new Int2ObjectOpenHashMap<>();
	private Configuration config;
	private String[] absoluteTransits;
	private String[] relativeTransits;

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
		String cat = DimstackMod.MODID + ".layers";
		config.addCustomCategoryComment(cat, "Each row is one teleportation rift betewen dimensions" + "\r\nto: start dimension where item and tests are ran" + "\r\nfrom: destination" + "\r\ncompare:  < means player.y < yLimit  " + "\r\nkey: what you must hold.  Empty for no item check");
		this.absoluteTransits = config.getStringList("TargetedTransitions", cat, new String[] { "0,1,>,200,3,0,20,0" }, "Simple layer transitions that target an exact location in the destination dimension.  [from,to,compare,ylimit,key meta,positionxyz]  ");

		this.relativeTransits = config.getStringList("RelativeTransitions", cat, new String[] {
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
				DimstackMod.LOGGER.error("Invalid key color entry {} will be ignored", s);
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
				DimstackMod.LOGGER.error("Invalid portal color entry {} will be ignored", s);
				continue;
			}

			int meta = Integer.parseInt(split[0]);
			int color = Integer.parseInt(split[1].contains("0x") ? split[1].substring(2) : split[1], split[1].contains("0x") ? 16 : 10);
			int color2 = Integer.parseInt(split[2].contains("0x") ? split[2].substring(2) : split[2], split[2].contains("0x") ? 16 : 10);

			dimPortalColors.put(meta, new int[] { color, color2 });
		}

		config.save();
		TransitManager.reload(this);
	}

	public String[] getRelativeTransits() {
		return relativeTransits;
	}

	public String[] getAbsoluteTransits() {
		return absoluteTransits;
	}

}
