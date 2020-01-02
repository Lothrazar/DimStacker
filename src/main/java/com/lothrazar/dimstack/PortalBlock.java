package com.lothrazar.dimstack;

import com.lothrazar.dimstack.transit.ActiveTransit;
import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class PortalBlock extends Block {

  public static final VoxelShape PORTAL_AABB = Block.makeCuboidShape(0, 0.4, 0, 1, 0.6, 1);

  public PortalBlock() {
    super(Block.Properties.create(Material.PORTAL).hardnessAndResistance(-1));
    //    this.setBlockUnbreakable(); 
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return PORTAL_AABB;
  }
  //  @Override
  //  public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
  //    return PORTAL_AABB;
  //  }

  @Override
  public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
    if (entity instanceof PlayerEntity && entity.isAlive()) {
      if (!world.isRemote && !entity.isOnePlayerRiding() && !entity.isBeingRidden() && entity.isNonBoss()) {
        PlayerEntity playerMP = (PlayerEntity) entity;
        if (playerMP.getCooldownTracker().hasCooldown(DimstackMod.PORTAL_I)) return;
        PortalTile tile = (PortalTile) world.getTileEntity(pos);
        Transit t = TransitManager.getTargetFor(tile);
        if (t == null || tile == null) return;
        //        playerMP.getServer().getw
        ServerWorld targetDim = playerMP.getServer().getWorld(DimensionType.getById(t.getTargetDim()));
        try {
          ActiveTransit teleporter = new ActiveTransit(targetDim, tile, t);
          teleporter.teleport(playerMP);
          playerMP.getCooldownTracker().setCooldown(DimstackMod.PORTAL_I, 20);
        }
        catch (Exception e) {
          DimstackMod.LOGGER.error("There has been an exception during an attempted teleportation.", e);
        }
      }
    }
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new PortalTile();
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.TRANSLUCENT;
  }
  //  @Override
  //  public boolean isOpaqueCube(BlockState state) {
  //    return false;
  //  }
  //  @Override
  //  public boolean isFullCube(BlockState state) {
  //    return false;
  //  }
  //  @Override
  //  public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {}

  @Override
  public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 0;
  }

  @Override
  public int getLightValue(BlockState state) {
    return 15;
  }
}
