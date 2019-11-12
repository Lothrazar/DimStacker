package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.PortalTile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ITeleporter;

public class ActiveTransit implements ITeleporter {

	protected WorldServer world;
	protected PortalTile source;
	protected Transit transit;
	protected BlockPos portalPos;

	public ActiveTransit(WorldServer world, PortalTile source, Transit transit) {
		this.world = world;
		this.source = source;
		this.transit = transit;
	}

	public void teleport(EntityPlayerMP player) {
		if (transit.isRelative()) {
			portalPos = createOrFindPortal();
			if (source.goesUpwards()) {
				teleport(player, transit.getTargetDim(), portalPos.getX() + 1.3D, portalPos.getY() + 1, portalPos.getZ() + 1.3D);
			} else {
				teleport(player, transit.getTargetDim(), portalPos.getX() + 0.5D, portalPos.getY() + -3, portalPos.getZ() + 0.5D);
			}
			TextComponentTranslation to = new TextComponentTranslation("dimstack." + DimensionType.getById(transit.getTargetDim()).getName() + ".name");
			player.sendMessage(new TextComponentTranslation("dimstack.teleport.info", to));
		} else {
			portalPos = transit.getTargetPos();
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					world.setBlockState(portalPos.add(x, 0, z), Blocks.STONEBRICK.getDefaultState());
				}
			}
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = 1; y < 4; y++)
						world.setBlockState(portalPos.add(x, y, z), Blocks.AIR.getDefaultState());
				}
			}
			TextComponentTranslation to = new TextComponentTranslation("dimstack." + DimensionType.getById(transit.getTargetDim()).getName() + ".name");
			player.sendMessage(new TextComponentTranslation("dimstack.teleport.info", to));
			teleport(player, transit.getTargetDim(), portalPos.getX() + 0.5, portalPos.getY() + 1, portalPos.getZ() + 0.5);
		}
	}

	private void teleport(EntityPlayerMP player, int dimension, double x, double y, double z) throws IllegalArgumentException {
		if (!player.capabilities.isCreativeMode) {
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 200, false, false));
		}
		if (this.world != null && this.world.getMinecraftServer() != null && this.world.getMinecraftServer().getPlayerList() != null) {
			PlayerList playerList = this.world.getMinecraftServer().getPlayerList();
			playerList.transferPlayerToDimension(player, dimension, this);
			this.world.playSound(null, x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.25F, this.world.rand.nextFloat() * 0.4F + 0.8F);
		} else {
			throw new IllegalArgumentException("Dimension: " + dimension + " doesn't exist, or the PlayerList was null.");
		}
	}

	@Override
	public void placeEntity(World world, Entity entity, float rotationYaw) {
		if (transit.isRelative()) {
			if (source.goesUpwards()) {
				entity.setLocationAndAngles(portalPos.getX() + 1.3D, portalPos.getY() + 1, portalPos.getZ() + 1.3D, rotationYaw, 90);
			} else {
				entity.setLocationAndAngles(portalPos.getX() + 0.5D, portalPos.getY() + -3, portalPos.getZ() + 0.5D, rotationYaw, 90);
			}
		} else entity.setLocationAndAngles(portalPos.getX() + 0.5D, portalPos.getY() + 1, portalPos.getZ() + 0.5D, rotationYaw, 90);
		entity.fallDistance = 0;
		entity.motionX = 0.0D;
		entity.motionY = 0.0D;
		entity.motionZ = 0.0D;
	}

	/**
	 * Looks for the existing portal pair, and if it does not find one, creates it.
	 */
	private BlockPos createOrFindPortal() {

		BlockPos destination;

		if (source.getTarget().equals(PortalTile.UNLINKED)) {

			BlockPos src = source.getPos();
			int x = (int) (src.getX() * transit.getRatio());
			int y = transit.getLanding();
			int z = (int) (src.getZ() * transit.getRatio());
			MutableBlockPos dest = new MutableBlockPos(x, y, z);

			if (source.goesUpwards()) { //We just went up, so search upwards.
				while (true) {
					if (y >= 255 || world.getBlockState(dest.setPos(x, y + 1, z)).getBlock() != Blocks.BEDROCK) {
						break;
					}
					y++;
				}
			} else { //We just went down, so search downwards.
				IBlockState state = world.getBlockState(dest);
				while (true) {
					if (y <= 0 || state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
						break;
					}
					state = world.getBlockState(dest.setPos(x, --y, z));
				}
			}

			destination = dest.toImmutable();
		} else destination = source.getTarget();

		IBlockState dest = world.getBlockState(destination);

		if (dest.getBlock() != DimstackMod.PORTAL) {
			world.setBlockState(destination, DimstackMod.PORTAL.getDefaultState());
			PortalTile tile = (PortalTile) world.getTileEntity(destination);
			tile.setGoesUpwards(!source.goesUpwards());
			tile.setTarget(source.getPos());
			tile.setTargetDim(source.getWorld().provider.getDimension());
			source.setTarget(tile.getPos());
			source.setTargetDim(tile.getWorld().provider.getDimension());
			TransitUtil.buildStructure(tile);
		}

		return destination;
	}
}
