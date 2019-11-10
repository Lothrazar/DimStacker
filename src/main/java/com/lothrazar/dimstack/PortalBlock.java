package com.lothrazar.dimstack;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class PortalBlock extends Block {

	public static final AxisAlignedBB PORTAL_AABB = new AxisAlignedBB(0, 0.4, 0, 1, 0.6, 1);

	public PortalBlock() {
		super(Material.PORTAL);
		this.setTranslationKey(DimstackMod.MODID + ".portal");
		this.setBlockUnbreakable();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return PORTAL_AABB;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		if (entity instanceof EntityPlayerMP && !entity.isDead) {
			if (!world.isRemote && !entity.isRiding() && !entity.isBeingRidden() && entity.isNonBoss()) {
				EntityPlayerMP playerMP = (EntityPlayerMP) entity;
				PortalTile tile = (PortalTile) world.getTileEntity(pos);
				PlayerTransmit t = DimstackMod.getTargetFor(tile);
				if (t == null || tile == null) return;
				WorldServer targetDim = playerMP.getServer().getWorld(t.to);
				try {
					if (t.relative) {
						BlockPos target = pos;
						float x = target.getX() * t.ratio;
						float z = target.getX() * t.ratio;
						target = new BlockPos((int) x, t.pos.getY(), (int) z);
						StackTeleporter teleporter = new StackTeleporter(targetDim, target);
						teleporter.teleportToDimension(tile, playerMP, t.to, !t.relative);
					} else if (t.pos != null) {
						StackTeleporter teleporter = new StackTeleporter(targetDim, t.pos);
						teleporter.teleportToDimension(tile, playerMP, t.to, !t.relative);
					}
				} catch (Exception e) {
					DimstackMod.logger.error("There has been an exception during an attempted teleportation.", e);
				}
			}
		}
	}

	public static void setDestination(World world, BlockPos pos, PlayerTransmit t) {
		if (t.relative) {
			BlockPos target = pos;
			float x = target.getX() * t.ratio;
			float z = target.getX() * t.ratio;
			target = new BlockPos((int) x, t.pos.getY(), (int) z);
			PortalTile tile = (PortalTile) world.getTileEntity(pos);
			if (tile != null) {
				tile.target = target;
				tile.targetDim = t.to;
			}
		} else if (t.pos != null) {
			PortalTile tile = (PortalTile) world.getTileEntity(pos);
			if (tile != null) {
				tile.target = t.pos;
				tile.targetDim = t.to;
			}
		}
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new PortalTile();
	}

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
	}

}
