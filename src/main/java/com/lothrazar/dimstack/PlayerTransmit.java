package com.lothrazar.dimstack;

import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

public class PlayerTransmit {

  @Override
  public String toString() {
    return "PlayerTransmit [from=" + from + ", to=" + to + ", greaterThan=" + greaterThan + ", yLimit=" + yLimit + ", key=" + key + ", pos=" + pos + "]";
  }

  int from = 0;
  int to = 1;
  //true: player.y > yLimit
  //false: player.y <= yLimit
  boolean greaterThan = false;
  int yLimit = 3;
  //  String door = "minecraft:bedrock"; 
  String key = "minecraft:stick";
  Item keyCached = null;
  @Nullable
  public BlockPos pos;
  public boolean relative = false;
  public float ratio = 1;
}
