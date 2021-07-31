package com.lothrazar.dimstack.util;

import com.lothrazar.dimstack.block.PortalTile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TransitUtil {

  /**
   * Creates the stone brick and glowstone structure that houses a portal.
   * 
   * @param tile
   *          The tile to generate this structure for.
   */
  public static void buildStructure(PortalTile tile) {
    Level world = tile.getLevel();
    BlockPos pos = tile.getBlockPos();
    for (int x = -2; x <= 2; x++) {
      for (int z = -2; z <= 2; z++) {
        for (int y = !tile.goesUpwards() ? 1 : -3; y <= (!tile.goesUpwards() ? 3 : -1); y++) {
          world.setBlock(pos.offset(x, y, z), Blocks.AIR.defaultBlockState(), 2);
        }
      }
    }
    for (int x = -2; x <= 2; x += 4) {
      for (int z = -2; z <= 2; z += 4) {
        for (int y = !tile.goesUpwards() ? 0 : -4; y <= (!tile.goesUpwards() ? 4 : 0); y++) {
          world.setBlock(pos.offset(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 2);
        }
      }
    }
    for (int x = -2; x <= 2; x++) {
      for (int z = -2; z <= 2; z++) {
        for (int y = !tile.goesUpwards() ? 0 : -4; y <= (!tile.goesUpwards() ? 4 : 0); y += 4) {
          if (x != 0 || y != 0 || z != 0) {
            world.setBlock(pos.offset(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 2);
          }
        }
      }
    }
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        if (!(x == 0 && z == 0)) {
          world.setBlockAndUpdate(pos.offset(x, 0, z), Blocks.GLOWSTONE.defaultBlockState());
        }
        world.setBlock(pos.offset(x, !!tile.goesUpwards() ? -4 : 4, z), Blocks.GLOWSTONE.defaultBlockState(), 2);
      }
    }
  }
}
