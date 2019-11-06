package com.lothrazar.dimstack;
public class PlayerTransmit {

  int from = 0;
  int to = 1;
  //true: player.y > yLimit
  //false: player.y <= yLimit
  boolean greaterThan = false;
  int yLimit = 3;
  //  String door = "minecraft:bedrock"; 
  String key = "minecraft:stick";
  public boolean setSpawnOnDestination;
}
