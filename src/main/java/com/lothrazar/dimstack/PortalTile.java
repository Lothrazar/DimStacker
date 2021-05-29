package com.lothrazar.dimstack;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PortalTile extends TileEntity {

  public static final BlockPos UNLINKED = new BlockPos(-1, -1, -1);
  private BlockPos target = UNLINKED;
  private boolean goesUp = false;

  public PortalTile() {
    super(DimstackMod.portalTile);
  }

  public BlockPos getTarget() {
    return target;
  }

  public boolean goesUpwards() {
    return goesUp;
  }

  public void setTarget(BlockPos pos) {
    this.target = pos;
    markDirty();
  }

  public void setGoesUpwards(boolean top) {
    this.goesUp = top;
    markDirty();
  }

  @Override
  public void read(BlockState bs, CompoundNBT tag) {
    target = BlockPos.fromLong(tag.getLong("target"));
    goesUp = tag.getBoolean("up");
    super.read(bs, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    tag.putLong("target", target.toLong());
    tag.putBoolean("up", goesUp);
    return super.write(tag);
  }
}
