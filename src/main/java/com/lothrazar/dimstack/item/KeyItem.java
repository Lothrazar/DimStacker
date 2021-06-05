package com.lothrazar.dimstack.item;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyItem extends Item {

  // only used for colour?
  public final String targetDimension;

  public KeyItem(Item.Properties prop, String dim) {
    super(prop.maxStackSize(1));
    targetDimension = dim;
  }
  //
  //  @Override
  //  public ActionResultType onItemUse(ItemUseContext context) {
  //    World world = context.getWorld();
  //    if (world.isRemote) {
  //      return ActionResultType.SUCCESS;
  //    }
  //    PlayerEntity player = context.getPlayer();
  //    BlockPos pos = context.getPos();
  //    Hand hand = context.getHand();
  //    
  //    Transit t = TransitManager.getTargetFor(world, pos, this);
  //    if (t != null) {
  //      world.setBlockState(pos, DimstackRegistry.PORTAL.get().getDefaultState());
  //      PortalTile tile = (PortalTile) world.getTileEntity(pos);
  //      tile.setTransit(t);
  //      tile.markDirty();
  //      player.getCooldownTracker().setCooldown(this, 300);
  //      player.getHeldItem(hand).shrink(1); // todo DoesConsume trigger in T
  //      TransitUtil.buildStructure(tile);
  //      return ActionResultType.SUCCESS;
  //    }
  //    return ActionResultType.FAIL;
  //  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    //    for (Transit t : TransitManager.getAllTransits()) {
    //      if (t.getKeyMeta() == stack.getOrCreateTag().getInt("keymeta")) {
    //        String from = I18n.format("dimstack." + t.getSourceDim().getPath() + ".name");
    //        String to = I18n.format("dimstack." + t.getTargetDim().getPath() + ".name");
    //        tooltip.add(new TranslationTextComponent(DimstackMod.MODID + ".tooltip", from, to));
    //      }
    //    }
  }
}
