package com.lothrazar.dimstack.transit;

import javax.annotation.Nullable;

import com.lothrazar.dimstack.DimstackMod;

import net.minecraft.util.math.BlockPos;

/**
 * A transit object.  A transit represents a connection between vertical dimensions.
 */
public class Transit {

	protected final int from;
	protected final int to;
	protected final boolean goesUpwards;
	protected final int yLimit;
	protected final BlockPos pos;
	protected final boolean relative;
	protected final float ratio;
	protected final int keyMeta;
	protected final int landing;

	public Transit(int from, int to, boolean top, int yLimit, BlockPos pos, boolean relative, float ratio, int keyMeta, int landing) {
		this.from = from;
		this.to = to;
		this.goesUpwards = top;
		this.yLimit = yLimit;
		this.pos = pos;
		this.relative = relative;
		this.ratio = ratio;
		this.keyMeta = keyMeta;
		this.landing = landing;
	}

	/**
	 * The id of the dimension this transit comes from.
	 */
	public int getSourceDim() {
		return from;
	}

	/**
	 * The id of the dimension this transit leads to.
	 */
	public int getTargetDim() {
		return to;
	}

	/**
	 * If this transit goes upwards into the connected dimension
	 * If this returns true, the portal is expected to be near the ceiling of the dim, whatever that may be.
	 */
	public boolean goesUpwards() {
		return goesUpwards;
	}

	/**
	 * The limit of how close the player must be to the top (or bottom) of the world to utilize this transit.
	 * This number is a y-value.  If {@link Transit#goesUpwards()} returns true, the player must be above this value, otherwise they must be below it.
	 */
	public int getLimit() {
		return yLimit;
	}

	/**
	 * If this transit does a relative transition or not.  If it does, then {@link Transit#getTargetPos()} will be null.
	 * In the event that it does not, the target pos will always be where the transit sends the player, and a return portal will not be created.
	 */
	public boolean isRelative() {
		return relative;
	}

	/**
	 * If {@link Transit#isRelative()} is false, returns the destination of this transit.  Otherwise, this returns null.
	 */
	@Nullable
	public BlockPos getTargetPos() {
		return pos;
	}

	/**
	 * The factor that will be applied to the source coordinates when travelling to a dimension, before looking for a destination.
	 * Only used if {@link Transit#isRelative()} is true.
	 */
	public float getRatio() {
		return ratio;
	}

	/**
	 * Which metadata on the {@link DimstackMod#KEY} item is required to open this portal.
	 */
	public int getKeyMeta() {
		return keyMeta;
	}

	/**
	 * Returns the y-level that the destination portal will spawn at, if {@link Transit#isRelative()} is true.
	 * Otherwise this is not relevant.
	 */
	public int getLanding() {
		return landing;
	}

	public static Transit.Builder builder() {
		return new Builder();
	}

	public static Transit fromString(String layer, boolean relative) {
		String[] lrs = layer.split(",");
		try {
			Transit.Builder builder = Transit.builder();
			builder.from(Integer.parseInt(lrs[0]));
			builder.to(Integer.parseInt(lrs[1]));
			builder.goesUpwards(">".equalsIgnoreCase(lrs[2]));
			builder.limit(Integer.parseInt(lrs[3]));
			builder.key(Integer.parseInt(lrs[4]));
			if (!relative) {
				int x = Integer.parseInt(lrs[5]), y = Integer.parseInt(lrs[6]), z = Integer.parseInt(lrs[7]);
				builder.pos(new BlockPos(x, y, z));
			} else {
				builder.ratio(Float.parseFloat(lrs[5]));
				builder.landing(Integer.parseInt(lrs[6]));
			}
			return builder.build();
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			throw new TransitParseException(layer, e);
		}

	}

	public static class Builder {

		protected int from;
		protected int to;
		protected boolean goesUpwards;
		protected int yLimit;
		protected BlockPos pos;
		protected boolean relative;
		protected float ratio;
		protected int keyMeta;
		protected int landing;

		public Builder() {

		}

		public Builder from(int dim) {
			this.from = dim;
			return this;
		}

		public Builder to(int dim) {
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

		public Builder key(int keyMeta) {
			this.keyMeta = keyMeta;
			return this;
		}

		public Builder landing(int landing) {
			this.landing = landing;
			return this;
		}

		public Transit build() {
			return new Transit(from, to, goesUpwards, yLimit, pos, relative, ratio, keyMeta, landing);
		}

	}

	public static class TransitParseException extends RuntimeException {

		String failed;
		Exception cause;

		public TransitParseException(String failed, Exception cause) {
			this.failed = failed;
			this.cause = cause;
		}

		public String getFailed() {
			return failed;
		}

		public Exception getCause() {
			return cause;
		}
	}

}
