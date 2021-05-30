package com.lothrazar.dimstack.client;

import com.lothrazar.dimstack.DimstackConfig;
import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.UtilWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = DimstackMod.MODID)
public class DimstackClient {

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Item e) {
    e.getItemColors().register((stack, tintIndex) -> {
      //convert to color now 
      if (stack.getItem() == DimstackRegistry.KEY.get()) {
        // ok
        if (tintIndex == 0) {
          String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
          if (DimstackConfig.DIMKEYCOLORS.containsKey(dim)) {
            return DimstackConfig.DIMKEYCOLORS.get(dim);
          }
          return 0xFF000000;
        }
        //layer 1 is overlay  
        //        int c = StorageBagItem.getColour(stack);
        return 0;
      }
      return -1;
    }, DimstackRegistry.KEY.get());
    e.getItemColors().register((stack, tintIndex) -> {
      if (stack.getItem() == DimstackRegistry.PORTAL_I.get()) {
        if (tintIndex == 0) {
          String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
          if (DimstackConfig.DIMPORTALCOLORS.containsKey(dim)) {
            return DimstackConfig.DIMPORTALCOLORS.get(dim);
          }
          return 0xFFFFFFFF;
        }
        //layer 1 is overlay  
        //        int c = TODO get from dimension.getColour(stack);
        return 0xFF00FF00;
      }
      return -1;
    }, DimstackRegistry.PORTAL_I.get());
  }

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Block e) {
    RenderTypeLookup.setRenderLayer(DimstackRegistry.PORTAL.get().getBlock(), RenderType.getTranslucent());
    e.getBlockColors().register((state, reader, pos, tintIndex) -> {
      //convert to color now
      if (state.getBlock() == DimstackRegistry.PORTAL.get()) {
        // ok
        if (tintIndex == 0) { //layer zero is outline, ignore this 
          String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
          if (DimstackConfig.DIMPORTALCOLORS.containsKey(dim)) {
            return DimstackConfig.DIMPORTALCOLORS.get(dim);
          }
          return 0xFFFFFFFF;
        }
        //layer 1 is overlay  
        //        int c = StorageBagItem.getColour(stack);
        return 0;
      }
      return -1;
    }, DimstackRegistry.PORTAL.get());
    e.getBlockColors().register((state, reader, pos, tintIndex) -> {
      //convert to color now
      if (state.getBlock() == DimstackRegistry.PORTAL.get()) {
        // ok
        if (tintIndex == 1) {
          String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
          if (DimstackConfig.DIMPORTALUNDERCOLORS.containsKey(dim)) {
            return DimstackConfig.DIMPORTALUNDERCOLORS.get(dim);
          }
          return 0xFFFFFFFF;
        }
        //layer 1 is overlay  
        //        int c = StorageBagItem.getColour(stack);
        return 0;
      }
      return -1;
    }, DimstackRegistry.PORTAL.get());
  }
}
