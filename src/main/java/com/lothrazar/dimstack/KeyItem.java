package com.lothrazar.dimstack;

import java.util.List;

import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;
import com.lothrazar.dimstack.transit.TransitUtil;

import net.minecraft.client.resources.I18n;
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
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return EnumActionResult.SUCCESS;
		Transit t = TransitManager.getTargetFor(world, pos);
		if (t != null && t.getKeyMeta() == player.getHeldItem(hand).getMetadata()) {
			world.setBlockState(pos, DimstackMod.PORTAL.getDefaultState());
			PortalTile tile = (PortalTile) world.getTileEntity(pos);
			tile.setGoesUpwards(t.goesUpwards());
			player.getCooldownTracker().setCooldown(this, 300);
			player.getHeldItem(hand).shrink(1);
			TransitUtil.buildStructure(tile);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
		for (Transit t : TransitManager.getAllTransits()) {
			if (t.getKeyMeta() == stack.getMetadata()) {
				String from = I18n.format("dimstack." + DimensionType.getById(t.getSourceDim()).getName() + ".name");
				String to = I18n.format("dimstack." + DimensionType.getById(t.getTargetDim()).getName() + ".name");
				tooltip.add(I18n.format(DimstackMod.MODID + ".tooltip", from, to));
			}
		}
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) for (Transit t : TransitManager.getAllTransits()) {
			items.add(new ItemStack(this, 1, t.getKeyMeta()));
		}
	}

}
