package com.lothrazar.dimstack.client;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.UtilWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = DimstackMod.MODID)
public class DimstackClient {

  public static void setup() {
    //    initColours();
  }

  @SubscribeEvent
  public static void models(ModelRegistryEvent e) {
    //    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(DimstackMod.PORTAL), 0, new ModelResourceLocation(DimstackMod.PORTAL.getRegistryName(), "normal"));
    //    for (Transit t : TransitManager.getAllTransits()) {
    //      ModelLoader.setCustomModelResourceLocation(DimstackMod.KEY, t.getKeyMeta(), new ModelResourceLocation(DimstackMod.KEY.getRegistryName(), "inventory"));
    //    }
  }

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Item e) {
    e.getItemColors().register((stack, tintIndex) -> {
      String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
      //convert to color now
      if (stack.getItem() == DimstackRegistry.KEY.get()) {
        // ok
        if (tintIndex == 0) { //layer zero is outline, ignore this 
          //          return 0xFFFFFFFF;
          return 0xFF00FF00;
        }
        //layer 1 is overlay  
        //        int c = StorageBagItem.getColour(stack);
        return 0;
      }
      return -1;
    }, DimstackRegistry.KEY.get());
    e.getItemColors().register((stack, tintIndex) -> {
      String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
      //convert to color now
      if (stack.getItem() == DimstackRegistry.PORTAL_I.get()) {
        // ok
        if (tintIndex == 0) { //layer zero is outline, ignore this 
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
      String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
      //convert to color now
      if (state.getBlock() == DimstackRegistry.PORTAL.get()) {
        // ok
        if (tintIndex == 0) { //layer zero is outline, ignore this 
          return 0xFFFFFFFF;
        }
        //layer 1 is overlay  
        //        int c = StorageBagItem.getColour(stack);
        return 0;
      }
      return -1;
    }, DimstackRegistry.PORTAL.get());
    //    e.getBlockColors().registerBlockColorHandler((state, world, pos, tint) -> {
    //      int dim = Minecraft.getMinecraft().world.provider.getDimension();
    //      if (DimstackMod.config.dimPortalColors.containsKey(dim)) {
    //        int[] colors = DimstackMod.config.dimPortalColors.get(dim);
    //        return colors[pos.getY() < 50 ? 0 : 1];
    //      }
    //      return -1;
    //    }, DimstackMod.PORTAL);
  }
}
