package com.lothrazar.dimstack.block;

import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.util.DimstackRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PortalTile extends BlockEntity {

  public static final BlockPos UNLINKED = new BlockPos(-1, -1, -1);
  private BlockPos target = UNLINKED;
  private Transit transit;

  public PortalTile(BlockPos pos, BlockState state) {
    super(DimstackRegistry.PORTAL_TILE.get(), pos, state);
  }

  public BlockPos getTarget() {
    return target;
  }

  public boolean goesUpwards() {
    return transit == null ? false : transit.goesUpwards();
  }

  public void setTarget(BlockPos pos) {
    this.target = pos;
    setChanged();
  }

  @Override
  public void load(CompoundTag tag) {
    if (tag.contains("transit")) {
      transit = new Transit(tag.getCompound("transit"));
    }
    target = BlockPos.of(tag.getLong("target"));
    super.load(tag);
  }

  @Override
  public CompoundTag save(CompoundTag tag) {
    if (transit != null) {
      tag.put("transit", transit.write());
    }
    tag.putLong("target", target.asLong());
    return super.save(tag);
  }

  public Transit getTransit() {
    return transit;
  }

  public void setTransit(Transit t) {
    this.transit = t;
  }
}
