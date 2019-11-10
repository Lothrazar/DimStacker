package com.lothrazar.dimstack;

import java.util.function.Predicate;

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
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

//thanks to mrbysco MIT
//https://github.com/Mrbysco/TelePastries/blob/044fd1b78a43cca9fbbc7a4bf07599dcde1d0c7c/src/main/java/com/mrbysco/telepastries/util/CakeTeleporter.java
public class StackTeleporter extends Teleporter {

	public static final Predicate<IBlockState> BR_OR_PORTAL = s -> s.getBlock() == Blocks.BEDROCK || s.getBlock() == DimstackMod.PORTAL;

	private BlockPos position;
	private PortalTile tile;

	public StackTeleporter(WorldServer world, BlockPos pos) {
		super(world);
		this.position = pos;
	}

	public void teleportToDimension(PortalTile source, EntityPlayerMP player, int dimension, boolean exact) {
		tile = source;
		if (source.targetDim == dimension && !source.target.equals(PortalTile.UNLINKED)) {
			//We have a pre-existing target, use it
			this.position = source.target;
		}
		if (!exact) {
			this.createSafetyBox(player, position);
			teleport(player, dimension, position.getX() + 1.5D, position.getY() + (!tile.top ? -3 : 1), position.getZ() + 1.5D);
		} else {
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					world.setBlockState(position.add(x, 0, z), Blocks.STONEBRICK.getDefaultState());
				}
			}
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					for (int y = 1; y < 4; y++)
						world.setBlockState(position.add(x, y, z), Blocks.AIR.getDefaultState());
				}
			}
			teleport(player, dimension, position.getX() + 0.5, position.getY() + 1, position.getZ() + 0.5);
		}
	}

	private void teleport(EntityPlayerMP player, int dimension, double x, double y, double z) throws IllegalArgumentException {
		this.world.playSound(null, x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.25F, this.world.rand.nextFloat() * 0.4F + 0.8F);
		if (!player.capabilities.isCreativeMode) {
			player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 200, false, false));
		}
		if (this.world != null && this.world.getMinecraftServer() != null && this.world.getMinecraftServer().getPlayerList() != null) {
			PlayerList playerList = this.world.getMinecraftServer().getPlayerList();
			playerList.transferPlayerToDimension(player, dimension, this);
			player.setPositionAndUpdate(x, y, z);
		} else {
			throw new IllegalArgumentException("Dimension: " + dimension + " doesn't exist! Or playerList error");
		}
	}

	@Override
	public void placeInPortal(Entity entityIn, float rotationYaw) {
		entityIn.setPosition(position.getX(), position.getY(), position.getZ());
		entityIn.motionX = 0.0D;
		entityIn.motionY = 0.0D;
		entityIn.motionZ = 0.0D;
	}

	//thanks to mrbysco MIT
	//https://github.com/Mrbysco/TelePastries/blob/044fd1b78a43cca9fbbc7a4bf07599dcde1d0c7c/src/main/java/com/mrbysco/telepastries/util/CakeTeleporter.java
	private void createSafetyBox(EntityPlayerMP playerIn, BlockPos position) {

		if (tile.target.equals(PortalTile.UNLINKED)) {
			if (tile.top) {
				position = new BlockPos(position.getX(), 0, position.getZ());
				IBlockState state = world.getBlockState(position);
				while (true) {
					if (position.getY() == 255 || state.getBlock() == Blocks.BEDROCK && world.getBlockState(position.up()).getBlock() != Blocks.BEDROCK || state.getBlock() == DimstackMod.PORTAL) {
						break;
					}
					state = world.getBlockState(position = position.up());
				}
			} else {
				position = new BlockPos(position.getX(), world.getActualHeight(), position.getZ());
				IBlockState state = world.getBlockState(position);
				while (true) {
					if (position.getY() == 0 || state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK || state.getBlock() == DimstackMod.PORTAL) {
						break;
					}
					state = world.getBlockState(position = position.down());
				}
			}
		} else position = tile.target;

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				if (tile.top) {
					for (int y = 0; y <= 4; y++)
						world.setBlockState(position.add(x, y, z), Blocks.STONEBRICK.getDefaultState());
				} else {
					for (int y = -4; y <= 0; y++)
						world.setBlockState(position.add(x, y, z), Blocks.STONEBRICK.getDefaultState());
				}
			}
		}

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (tile.top) {
					for (int y = 0; y <= 4; y++)
						world.setBlockState(position.add(x, y, z), Blocks.AIR.getDefaultState());
				} else {
					for (int y = -4; y <= 0; y++)
						world.setBlockState(position.add(x, y, z), Blocks.AIR.getDefaultState());
				}
			}
		}

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				world.setBlockState(position.add(x, 0, z), Blocks.STONEBRICK.getDefaultState());
				world.setBlockState(position.add(x, !tile.top ? -4 : 4, z), Blocks.STONEBRICK.getDefaultState());
			}
		}

		world.setBlockState(position, DimstackMod.PORTAL.getDefaultState());
		PortalTile dest = (PortalTile) world.getTileEntity(position);
		dest.top = !tile.top;
		dest.target = tile.getPos();
		dest.targetDim = tile.getWorld().provider.getDimension();
		tile.target = dest.getPos();
		tile.targetDim = dest.getWorld().provider.getDimension();

		this.position = tile.target;
	}
}
