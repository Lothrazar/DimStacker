package com.lothrazar.dimstack;

import net.minecraft.util.math.BlockPos;

public class PlayerTransmit {

	@Override
	public String toString() {
		return "[from=" + from + ", to=" + to + ", yLimit=" + yLimit + ", key=" + keyMeta + ", pos=" + pos + "]";
	}

	int from = 0;
	int to = 1;
	//true: player.y > yLimit
	//false: player.y <= yLimit
	boolean greaterThan = false;
	int yLimit = 3;
	//  String door = "minecraft:bedrock";
	public BlockPos pos;
	public boolean relative = false;
	public float ratio = 1;
	public int keyMeta;
}
