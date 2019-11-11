package com.lothrazar.dimstack.transit;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.lothrazar.dimstack.DimConfig;
import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.PortalTile;
import com.lothrazar.dimstack.transit.Transit.TransitParseException;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TransitManager {

	private static final List<Transit> TRANSITS = new ArrayList<>();

	/**
	 * Gets the transmit handler for a given location.
	 */
	@Nullable
	public static Transit getTargetFor(World world, BlockPos pos) {
		for (Transit t : TRANSITS) {
			int playerY = pos.getY();
			if (t.getSourceDim() == world.provider.getDimension()) {
				if (t.goesUpwards() && playerY > t.yLimit) {
					return t;
				} else if (!t.goesUpwards() && playerY <= t.yLimit) return t;
			}
		}
		return null;
	}

	/**
	 * Gets the transmit handler for a given tile.
	 */
	@Nullable
	public static Transit getTargetFor(PortalTile tile) {
		for (Transit t : TRANSITS) {
			if (t.getSourceDim() == tile.getWorld().provider.getDimension()) {
				if (t.goesUpwards() == tile.goesUpwards()) return t;
			}
		}
		return null;
	}

	public static List<Transit> getAllTransits() {
		return TRANSITS;
	}

	public static void reload(DimConfig config) {
		TRANSITS.clear();
		for (String layer : config.getAbsoluteTransits()) {
			try {
				TRANSITS.add(Transit.fromString(layer, false));
			} catch (TransitParseException e) {
				DimstackMod.LOGGER.error("Invalid specific transition {} will be ignored.", e.getFailed());
				e.getCause().printStackTrace();
			}
		}
		for (String layer : config.getRelativeTransits()) {
			try {
				TRANSITS.add(Transit.fromString(layer, true));
			} catch (TransitParseException e) {
				DimstackMod.LOGGER.error("Invalid relative transition {} will be ignored.", e.getFailed());
				e.getCause().printStackTrace();
			}
		}

	}

}
