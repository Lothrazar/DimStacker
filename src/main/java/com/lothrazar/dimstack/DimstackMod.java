package com.lothrazar.dimstack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

@Mod(DimstackMod.MODID)
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

  public DimstackMod() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    //only for server starting
    MinecraftForge.EVENT_BUS.register(this);
    //	    config = new ConfigManager(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

  //	@EventHandler
  //	public void preInit(FMLPreInitializationEvent e) {
  //		config = new DimConfig(new Configuration(e.getSuggestedConfigurationFile()));
  //		MinecraftForge.EVENT_BUS.register(this);
  //		GameRegistry.registerTileEntity(PortalTile.class, new ResourceLocation(MODID, "portal"));
  //	}
  private void setup(final FMLCommonSetupEvent event) {
    //now all blocks/items exist
  }

  @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {

    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> e) {
      // register a new block here
      //      event.getRegistry().register(___);
      e.getRegistry().register(new PortalBlock().setRegistryName("portal"));
    }

    @SubscribeEvent
    public static void onItemsRegistry(RegistryEvent.Register<Item> e) {
      Item.Properties properties = new Item.Properties().group(ItemGroup.MISC);// tab group
      e.getRegistry().register(new KeyItem(properties).setRegistryName("key"));
      e.getRegistry().register(new BlockItem(PORTAL, properties).setRegistryName(PORTAL.getRegistryName()));
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(RegistryEvent.Register<TileEntityType<?>> event) {
      IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
      // do the PortalTile.class here
      r.register(TileEntityType.Builder.create(PortalTile::new, PORTAL).build(null).setRegistryName("portal"));
    }
  }
  //  @SubscribeEvent
  //  public void register(Register<Item> e) {
  //  }
  //
  //
  //  @EventHandler
  //  public void init(FMLInitializationEvent event) {
  //    MinecraftForge.EVENT_BUS.register(config);
  //  }
}
