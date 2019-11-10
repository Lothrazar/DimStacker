package com.lothrazar.dimstack;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class KeyItem extends Item {

	public KeyItem() {
		this.setHasSubtypes(true);
		this.setTranslationKey(DimstackMod.MODID + ".key");
		this.setCreativeTab(CreativeTabs.MISC);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return EnumActionResult.SUCCESS;
		PlayerTransmit t = DimstackMod.getTargetFor(world, pos);
		if (t != null && t.keyMeta == player.getHeldItem(hand).getMetadata()) {
			world.setBlockState(pos, DimstackMod.PORTAL.getDefaultState());
			PortalTile tile = (PortalTile) world.getTileEntity(pos);
			tile.top = t.greaterThan;
			player.getCooldownTracker().setCooldown(this, 300);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
		for (PlayerTransmit t : DimstackMod.config.emitters) {
			if (t.keyMeta == stack.getMetadata()) {
        DimensionType from = DimensionType.getById(t.from);
        DimensionType to = DimensionType.getById(t.to);
        String fromstr = t.from + "";
        String tostr = t.to + "";
        if (from != null) {
          fromstr = from.getName();
        }
        if (to != null) {
          tostr = to.getName();
        }
			  tooltip.add(DimstackMod.lang(DimstackMod.MODID + ".tooltip") + " [" + fromstr+"/"+tostr+ "]");}
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) for (PlayerTransmit t : DimstackMod.config.emitters) {
			items.add(new ItemStack(this, 1, t.keyMeta));
		}
	}

}
