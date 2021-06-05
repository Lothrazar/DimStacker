package com.lothrazar.dimstack;

import com.lothrazar.dimstack.event.ClientEvents;
import com.lothrazar.dimstack.event.ItemUseHandler;
import com.lothrazar.dimstack.transit.TransitManager;
import com.lothrazar.dimstack.util.DimstackRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(DimstackMod.MODID)
public class DimstackMod {

  //BEFORE RELEASE
  //1. tooltips 
  //- make cache when transit is made. 
  //- key lists every transit that applies
  //2. Config file
  //3. add key item id -> dimension id 1:1 in config
  //   so they can use like gold nugget as a key
  //   OR: dynamic keys
  // just have one generic_key item id, NBT color and recipe
  //  FireworkRocketRecipe y;
  public static final String MODID = "dimstack";
  public static final Logger LOGGER = LogManager.getLogger(MODID);

  public DimstackMod() {
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    DimstackRegistry.BLOCKS.register(eventBus);
    DimstackRegistry.ITEMS.register(eventBus);
    DimstackRegistry.TILE_ENTITIES.register(eventBus);
    DimstackConfig.setup(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    MinecraftForge.EVENT_BUS.register(new ItemUseHandler());
  }

  private void setupClient(final FMLClientSetupEvent event) {
    MinecraftForge.EVENT_BUS.register(new ClientEvents());
  }

  private void setup(final FMLCommonSetupEvent event) {
    TransitManager.reload();
  }
}
