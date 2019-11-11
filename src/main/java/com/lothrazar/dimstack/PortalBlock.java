package com.lothrazar.dimstack;

import com.lothrazar.dimstack.transit.ActiveTransit;
import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;

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
				if (playerMP.getCooldownTracker().hasCooldown(DimstackMod.PORTAL_I)) return;
				PortalTile tile = (PortalTile) world.getTileEntity(pos);
				Transit t = TransitManager.getTargetFor(tile);
				if (t == null || tile == null) return;
				WorldServer targetDim = playerMP.getServer().getWorld(t.getTargetDim());
				try {
					ActiveTransit teleporter = new ActiveTransit(targetDim, tile, t);
					teleporter.teleport(playerMP);
					playerMP.getCooldownTracker().setCooldown(DimstackMod.PORTAL_I, 20);
				} catch (Exception e) {
					DimstackMod.LOGGER.error("There has been an exception during an attempted teleportation.", e);
				}
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

	@Override
	public int getLightOpacity(IBlockState state) {
		return 0;
	}

	@Override
	public int getLightValue(IBlockState state) {
		return 15;
	}

}
