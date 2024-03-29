package com.lothrazar.dimstack.event;

import com.lothrazar.dimstack.transit.TransitManager;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {

  @SubscribeEvent
  public void onTool(ItemTooltipEvent event) {
    List<Component> tips = TransitManager.getTooltip(event.getItemStack());
    if (tips != null) {
      event.getToolTip().addAll(tips);
    }
  }
}
