package com.lothrazar.dimstack;

import com.lothrazar.dimstack.item.KeyItem;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.UtilWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = DimstackMod.MODID)
public class ClientColourRegistry {

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Item e) {
    e.getItemColors().register((stack, tintIndex) -> {
      if (tintIndex == 0 && stack.getItem() instanceof KeyItem) {
        KeyItem key = (KeyItem) stack.getItem();
        if (DimstackConfig.DIMKEYCOLORS.containsKey(key.targetDimension)) {
          return DimstackConfig.DIMKEYCOLORS.get(key.targetDimension);
        }
        else {
          DimstackMod.LOGGER.error("key color missing : " + key.targetDimension);
        }
      }
      return -1;
    }, DimstackRegistry.OVERWORLD_KEY.get(),
        DimstackRegistry.END_KEY.get(),
        DimstackRegistry.NETHER_KEY.get(),
        //modded keys
        DimstackRegistry.LOSTCITIES_KEY.get(),
        DimstackRegistry.GAIA_KEY.get(),
        DimstackRegistry.UNDERGARDEN_KEY.get(), DimstackRegistry.TWILIGHT_KEY.get());
    e.getItemColors().register((stack, tintIndex) -> {
      if (tintIndex == 0) {
        String dim = UtilWorld.dimensionToString(Minecraft.getInstance().level);
        if (DimstackConfig.DIMPORTALCOLORS.containsKey(dim)) {
          return DimstackConfig.DIMPORTALCOLORS.get(dim);
        }
        else {
          DimstackMod.LOGGER.error("Item color missing : " + dim);
        }
      }
      return -1;
    }, DimstackRegistry.PORTAL_I.get());
  }

  @SubscribeEvent
  public static void colors(ColorHandlerEvent.Block e) {
    ItemBlockRenderTypes.setRenderLayer(DimstackRegistry.PORTAL.get(), RenderType.translucent());
    e.getBlockColors().register((state, reader, pos, tintIndex) -> {
      if (tintIndex == 0) {
        String dim = UtilWorld.dimensionToString(Minecraft.getInstance().level);
        if (DimstackConfig.DIMPORTALCOLORS.containsKey(dim)) {
          return DimstackConfig.DIMPORTALCOLORS.get(dim);
        }
      }
      return -1;
    }, DimstackRegistry.PORTAL.get());
  }
}
