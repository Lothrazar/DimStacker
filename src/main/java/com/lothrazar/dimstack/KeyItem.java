package com.lothrazar.dimstack;

import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;
import com.lothrazar.dimstack.transit.TransitUtil;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyItem extends Item {

  public KeyItem(Item.Properties prop) {
    super(prop.maxStackSize(1));
    //		this.setHasSubtypes(true); 
  }

  @Override
  public ActionResultType onItemUse(ItemUseContext context) {
    World world = context.getWorld();
    if (world.isRemote) return ActionResultType.SUCCESS;
    PlayerEntity player = context.getPlayer();
    BlockPos pos = context.getPos();
    Hand hand = context.getHand();
    Transit t = TransitManager.getTargetFor(world, pos);
    if (t != null && t.getKeyMeta() == player.getHeldItem(hand).getOrCreateTag().getInt("keymeta")) {
      world.setBlockState(pos, DimstackMod.PORTAL.getDefaultState());
      PortalTile tile = (PortalTile) world.getTileEntity(pos);
      tile.setGoesUpwards(t.goesUpwards());
      player.getCooldownTracker().setCooldown(this, 300);
      player.getHeldItem(hand).shrink(1);
      TransitUtil.buildStructure(tile);
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.FAIL;
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void addInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
    for (Transit t : TransitManager.getAllTransits()) {
      if (t.getKeyMeta() == stack.getOrCreateTag().getInt("keymeta")) {
        String from = I18n.format("dimstack." + t.getSourceDim().getPath() + ".name");
        String to = I18n.format("dimstack." + t.getTargetDim().getPath() + ".name");
        tooltip.add(new TranslationTextComponent(DimstackMod.MODID + ".tooltip", from, to));
      }
    }
  }
  //
  //  @Override
  //  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
  //    if (this.isInCreativeTab(tab)) for (Transit t : TransitManager.getAllTransits()) {
  //      items.add(new ItemStack(this, 1, t.getKeyMeta()));
  //    }
  //  }
}
