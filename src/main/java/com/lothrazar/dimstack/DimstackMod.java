package com.lothrazar.dimstack;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DimstackMod.MODID, updateJSON = "https://raw.githubusercontent.com/Lothrazar/DimStacker/master/update.json")
public class DimstackMod {

	public static final String MODID = "dimstack";
	static Logger logger;
	static DimConfig config;

	@ObjectHolder("dimstack:portal")
	public static final PortalBlock PORTAL = null;

	@ObjectHolder("dimstack:key")
	public static final KeyItem KEY = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		config = new DimConfig(new Configuration(event.getSuggestedConfigurationFile()));
	}

	@EventHandler
	public void preinit(FMLPreInitializationEvent e) {
		MinecraftForge.EVENT_BUS.register(this);
		if (e.getSide() == Side.CLIENT) MinecraftForge.EVENT_BUS.register(new DimstackClient());
		GameRegistry.registerTileEntity(PortalTile.class, new ResourceLocation(MODID, "portal"));
	}

	@SubscribeEvent
	public void register(Register<Item> e) {
		e.getRegistry().register(new KeyItem().setRegistryName("key"));
		e.getRegistry().register(new ItemBlock(PORTAL).setRegistryName(PORTAL.getRegistryName()));
	}

	@SubscribeEvent
	public void registerB(Register<Block> e) {
		e.getRegistry().register(new PortalBlock().setRegistryName("portal"));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(config);
	}

	@SideOnly(Side.CLIENT)
	public static String lang(String message) {
		return I18n.format(message);
	}

	public static void sendStatusMessage(EntityPlayer player, String string) {
		player.sendMessage(new TextComponentTranslation(string));
	}

	/**
	 * Gets the transmit handler for a given location.
	 */
	@Nullable
	public static PlayerTransmit getTargetFor(World world, BlockPos pos) {
		for (PlayerTransmit t : DimstackMod.config.transmits) {
			int playerY = pos.getY();
			if (t.from == world.provider.getDimension()) {
				if (t.greaterThan && playerY > t.yLimit) {
					return t;
				} else if (!t.greaterThan && playerY <= t.yLimit) { return t; }
			}
		}
		return null;
	}

}
