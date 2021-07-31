package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackMod;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;

/**
 * A transit object. A transit represents a connection between vertical dimensions.
 */
public class Transit {

  protected ResourceLocation itemId;
  protected ResourceLocation from;
  protected ResourceLocation to;
  protected boolean goesUpwards;
  protected int yLimit;
  protected BlockPos pos;
  protected boolean relative;
  protected float ratio;
  protected int landing;

  public Transit(ResourceLocation itemId, ResourceLocation from, ResourceLocation to, boolean top, int yLimit, BlockPos pos, boolean relative, float ratio, int landing) {
    this.itemId = itemId;
    this.from = from;
    this.to = to;
    this.goesUpwards = top;
    this.yLimit = yLimit;
    this.pos = pos;
    this.relative = relative;
    this.ratio = ratio;
    this.landing = landing;
  }

  public Transit(CompoundTag tag) {
    read(tag);
  }

  public Transit() {}

  public void read(CompoundTag tag) {
    if (tag.contains("item")) {
      itemId = ResourceLocation.tryParse(tag.getString("item"));
    }
    from = ResourceLocation.tryParse(tag.getString("from"));
    to = ResourceLocation.tryParse(tag.getString("to"));
    goesUpwards = tag.getBoolean("up");
    yLimit = tag.getInt("ylimit");
    landing = tag.getInt("landing");
    ratio = tag.getFloat("ratio");
    if (tag.contains("pos")) {
      pos = NbtUtils.readBlockPos(tag.getCompound("pos"));
    }
  }

  public CompoundTag write() {
    CompoundTag tag = new CompoundTag();
    if (itemId != null) {
      tag.putString("item", itemId.toString());
    }
    tag.putString("from", from.toString());
    tag.putString("to", to.toString());
    tag.putBoolean("up", goesUpwards);
    tag.putInt("ylimit", yLimit);
    tag.putInt("landing", landing);
    tag.putFloat("ratio", ratio);
    if (pos != null) {
      tag.put("pos", NbtUtils.writeBlockPos(pos));
    }
    return tag;
  }

  /**
   * The id of the dimension this transit comes from.
   */
  public ResourceLocation getSourceDim() {
    return from;
  }

  /**
   * The id of the dimension this transit leads to.
   */
  public ResourceLocation getTargetDim() {
    return to;
  }

  /**
   * If this transit goes upwards into the connected dimension If this returns true, the portal is expected to be near the ceiling of the dim, whatever that may be.
   */
  public boolean goesUpwards() {
    return goesUpwards;
  }

  /**
   * The limit of how close the player must be to the top (or bottom) of the world to utilize this transit. This number is a y-value. If {@link Transit#goesUpwards()} returns true, the player must be
   * above this value, otherwise they must be below it.
   */
  public int getLimit() {
    return yLimit;
  }

  /**
   * If this transit does a relative transition or not. If it does, then {@link Transit#getTargetPos()} will be null. In the event that it does not, the target pos will always be where the transit
   * sends the player, and a return portal will not be created.
   */
  public boolean isRelative() {
    return relative;
  }

  /**
   * If {@link Transit#isRelative()} is false, returns the destination of this transit. Otherwise, this returns null.
   */
  @Nullable
  public BlockPos getTargetPos() {
    return pos;
  }

  /**
   * The factor that will be applied to the source coordinates when travelling to a dimension, before looking for a destination. Only used if {@link Transit#isRelative()} is true.
   */
  public float getRatio() {
    return ratio;
  }

  /**
   * Returns the y-level that the destination portal will spawn at, if {@link Transit#isRelative()} is true. Otherwise this is not relevant.
   */
  public int getLanding() {
    return landing;
  }

  public static Transit.Builder builder() {
    return new Builder();
  }

  public static Transit fromString(String layer, boolean relative) {
    String[] lrs = layer.split(",");
    Transit.Builder builder = Transit.builder();
    try {
      builder.item(ResourceLocation.tryParse(lrs[0]));
      builder.from(ResourceLocation.tryParse(lrs[1]));
      builder.to(ResourceLocation.tryParse(lrs[2]));
      builder.goesUpwards(">".equalsIgnoreCase(lrs[3]));
      builder.limit(Integer.parseInt(lrs[4]));
      if (!relative) {
        int x = Integer.parseInt(lrs[5]), y = Integer.parseInt(lrs[6]), z = Integer.parseInt(lrs[7]);
        builder.pos(new BlockPos(x, y, z));
      }
      else {
        builder.ratio(Float.parseFloat(lrs[5]));
        builder.landing(Integer.parseInt(lrs[6]));
      }
      return builder.build();
    }
    catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      DimstackMod.LOGGER.error("Rift problem " + layer + " || " + builder.build(), e);
      return null;
    }
  }

  @Override
  public String toString() {
    return "Transit [from=" + from + ", to=" + to + ", goesUpwards=" + goesUpwards + ", yLimit=" + yLimit +
        ", pos=" + pos + ", relative=" + relative + ", ratio=" + ratio + ", landing=" + landing + "]";
  }

  public static class Builder {

    protected ResourceLocation itemId;
    protected ResourceLocation from;
    protected ResourceLocation to;
    protected boolean goesUpwards;
    protected int yLimit;
    protected BlockPos pos;
    protected boolean relative = true; //assumed true until pos is set
    protected float ratio;
    protected int landing;

    public Builder() {}

    public Builder item(ResourceLocation dim) {
      this.itemId = dim;
      return this;
    }

    public Builder from(ResourceLocation dim) {
      this.from = dim;
      return this;
    }

    public Builder to(ResourceLocation dim) {
      this.to = dim;
      return this;
    }

    public Builder goesUpwards(boolean goesUpwards) {
      this.goesUpwards = goesUpwards;
      return this;
    }

    public Builder limit(int limit) {
      this.yLimit = limit;
      return this;
    }

    public Builder pos(BlockPos pos) {
      this.pos = pos;
      this.relative = false;
      return this;
    }

    public Builder ratio(float ratio) {
      this.ratio = ratio;
      this.relative = true;
      return this;
    }

    public Builder landing(int landing) {
      this.landing = landing;
      return this;
    }

    public Transit build() {
      return new Transit(itemId, from, to, goesUpwards, yLimit, pos, relative, ratio, landing);
    }
  }
}
