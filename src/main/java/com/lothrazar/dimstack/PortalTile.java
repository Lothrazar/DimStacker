package com.lothrazar.dimstack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PortalTile extends TileEntity {

	public static final BlockPos UNLINKED = new BlockPos(-1, -1, -1);

	private BlockPos target = UNLINKED;
	private int targetDim = 0;
	private boolean goesUp = false;

	public PortalTile() {
	}

	public BlockPos getTarget() {
		return target;
	}

	public int getTargetDim() {
		return targetDim;
	}

	public boolean goesUpwards() {
		return goesUp;
	}

	public void setTarget(BlockPos pos) {
		this.target = pos;
		markDirty();
	}

	public void setTargetDim(int dim) {
		this.targetDim = dim;
		markDirty();
	}

	public void setGoesUpwards(boolean top) {
		this.goesUp = top;
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		target = BlockPos.fromLong(tag.getLong("target"));
		targetDim = tag.getInteger("targetdim");
		goesUp = tag.getBoolean("up");
		super.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setLong("target", target.toLong());
		tag.setInteger("targetdim", targetDim);
		tag.setBoolean("up", goesUp);
		return super.writeToNBT(tag);
	}

}
