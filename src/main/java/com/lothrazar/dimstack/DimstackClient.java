package com.lothrazar.dimstack;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(value = Dist.CLIENT, modid = DimstackMod.MODID)
public class DimstackClient {

  @SubscribeEvent
  public static void models(ModelRegistryEvent e) {
    //    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(DimstackMod.PORTAL), 0, new ModelResourceLocation(DimstackMod.PORTAL.getRegistryName(), "normal"));
    //    for (Transit t : TransitManager.getAllTransits()) {
    //      ModelLoader.setCustomModelResourceLocation(DimstackMod.KEY, t.getKeyMeta(), new ModelResourceLocation(DimstackMod.KEY.getRegistryName(), "inventory"));
    //    }
  }

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Item e) {
    e.getItemColors().registerItemColorHandler((s, m) -> {
      if (DimstackMod.config.dimKeyColors.containsKey(s.getMetadata())) {
        return DimstackMod.config.dimKeyColors.get(s.getMetadata());
      }
      return -1;
    }, DimstackMod.KEY);
    e.getItemColors().registerItemColorHandler((stack, tint) -> {
      int dim = Minecraft.getMinecraft().world.provider.getDimension();
      if (DimstackMod.config.dimPortalColors.containsKey(dim)) {
        int[] colors = DimstackMod.config.dimPortalColors.get(dim);
        return colors[Minecraft.getMinecraft().player.posY < 50 ? 0 : 1];
      }
      return -1;
    }, DimstackMod.PORTAL);
  }

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Block e) {
    e.getBlockColors().registerBlockColorHandler((state, world, pos, tint) -> {
      int dim = Minecraft.getMinecraft().world.provider.getDimension();
      if (DimstackMod.config.dimPortalColors.containsKey(dim)) {
        int[] colors = DimstackMod.config.dimPortalColors.get(dim);
        return colors[pos.getY() < 50 ? 0 : 1];
      }
      return -1;
    }, DimstackMod.PORTAL);
  }

  public static void setup() {
    initColours();
  }

  private static void initColours() {
    Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
      String dim = UtilWorld.dimensionToString(Minecraft.getInstance().world);
      //convert to color now
      if (stack.getItem() == Items.ACACIA_BOAT) {
        // ok
        if (tintIndex == 0) { //layer zero is outline, ignore this 
          return 0xFFFFFFFF;
        }
        //layer 1 is overlay  
        //        int c = StorageBagItem.getColour(stack);
        return 0;
      }
      return -1;
    }, Items.ACACIA_BOAT);
  }
}
