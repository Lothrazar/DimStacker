package com.lothrazar.dimstack.block;

import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.util.DimstackRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PortalTile extends TileEntity {

  public static final BlockPos UNLINKED = new BlockPos(-1, -1, -1);
  private BlockPos target = UNLINKED;
  private Transit transit;

  public PortalTile() {
    super(DimstackRegistry.PORTAL_TILE.get());
  }

  public BlockPos getTarget() {
    return target;
  }

  public boolean goesUpwards() {
    return transit == null ? false : transit.goesUpwards();
  }

  public void setTarget(BlockPos pos) {
    this.target = pos;
    markDirty();
  }

  @Override
  public void read(BlockState bs, CompoundNBT tag) {
    if (tag.contains("transit")) {
      transit = new Transit(tag.getCompound("transit"));
    }
    target = BlockPos.fromLong(tag.getLong("target"));
    super.read(bs, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    if (transit != null) {
      tag.put("transit", transit.write());
    }
    tag.putLong("target", target.toLong());
    return super.write(tag);
  }

  public Transit getTransit() {
    return transit;
  }

  public void setTransit(Transit t) {
    this.transit = t;
  }
}
