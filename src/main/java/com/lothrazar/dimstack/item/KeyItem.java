package com.lothrazar.dimstack.item;

import net.minecraft.item.Item;

public class KeyItem extends Item {

  // only used for colour
  public final String targetDimension;

  public KeyItem(Item.Properties prop, String dim) {
    super(prop.maxStackSize(1));
    targetDimension = dim;
  }
}
