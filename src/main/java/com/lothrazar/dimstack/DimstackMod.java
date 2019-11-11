package com.lothrazar.dimstack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
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

@Mod(modid = DimstackMod.MODID, updateJSON = "https://raw.githubusercontent.com/Lothrazar/DimStacker/master/update.json")
public class DimstackMod {

	public static final String MODID = "dimstack";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static DimConfig config;

	@ObjectHolder("dimstack:portal")
	public static final PortalBlock PORTAL = null;

	@ObjectHolder("dimstack:portal")
	public static final Item PORTAL_I = null;

	@ObjectHolder("dimstack:key")
	public static final KeyItem KEY = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		config = new DimConfig(new Configuration(e.getSuggestedConfigurationFile()));
		MinecraftForge.EVENT_BUS.register(this);
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

}
