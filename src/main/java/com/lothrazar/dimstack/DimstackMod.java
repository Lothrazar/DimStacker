package com.lothrazar.dimstack;

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

  public static final String MODID = "dimstack";
  public static final Logger LOGGER = LogManager.getLogger(MODID);

  public DimstackMod() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    //only for server starting
    MinecraftForge.EVENT_BUS.register(this);
    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    DimstackRegistry.BLOCKS.register(eventBus);
    DimstackRegistry.ITEMS.register(eventBus);
    DimstackRegistry.TILE_ENTITIES.register(eventBus);
    DimstackConfig.setup(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

  private void setupClient(final FMLClientSetupEvent event) {
    //    DimstackClient.setup();
  }

  private void setup(final FMLCommonSetupEvent event) {
    //now all blocks/items exist
  }
}
