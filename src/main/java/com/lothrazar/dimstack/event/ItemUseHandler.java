package com.lothrazar.dimstack.event;

import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.TransitUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemUseHandler {

  @SubscribeEvent
  public void onUse(PlayerInteractEvent.RightClickBlock event) {
    ItemStack key = event.getItemStack();
    World world = event.getWorld();
    PlayerEntity player = event.getPlayer();
    BlockPos pos = event.getPos();
    Transit t = TransitManager.getTargetFor(world, pos, key.getItem());
    if (t != null) {
      world.setBlockState(pos, DimstackRegistry.PORTAL.get().getDefaultState());
      PortalTile tile = (PortalTile) world.getTileEntity(pos);
      tile.setTransit(t);
      tile.markDirty();
      player.getCooldownTracker().setCooldown(key.getItem(), 300);
      key.shrink(1); // todo DoesConsume trigger in config?
      TransitUtil.buildStructure(tile);
    }
  }
}
