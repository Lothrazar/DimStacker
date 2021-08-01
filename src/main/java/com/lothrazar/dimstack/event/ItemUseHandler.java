package com.lothrazar.dimstack.event;

import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.TransitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemUseHandler {

  @SubscribeEvent
  public void onUse(PlayerInteractEvent.RightClickBlock event) {
    ItemStack key = event.getItemStack();
    Level world = event.getWorld();
    Player player = event.getPlayer();
    BlockPos pos = event.getPos();
    Transit t = TransitManager.getTargetFor(world, pos, key.getItem());
    if (t != null) {
      world.setBlockAndUpdate(pos, DimstackRegistry.PORTAL.get().defaultBlockState());
      PortalTile tile = (PortalTile) world.getBlockEntity(pos);
      tile.setTransit(t);
      tile.setChanged();
      player.getCooldowns().addCooldown(key.getItem(), 300);
      key.shrink(1); // todo DoesConsume trigger in config?
      TransitUtil.buildStructure(tile);
    }
  }
}
