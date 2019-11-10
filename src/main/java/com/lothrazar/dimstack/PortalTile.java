package com.lothrazar.dimstack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PortalTile extends TileEntity {

	public static final BlockPos UNLINKED = new BlockPos(-1, -1, -1);

	public BlockPos target = UNLINKED;
	public int targetDim = 0;
	public boolean top = false;

	public PortalTile() {

	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		target = BlockPos.fromLong(tag.getLong("target"));
		targetDim = tag.getInteger("targetdim");
		top = tag.getBoolean("top");
		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setLong("target", target.toLong());
		tag.setInteger("targetdim", targetDim);
		tag.setBoolean("top", top);
		return super.writeToNBT(tag);
	}

}
