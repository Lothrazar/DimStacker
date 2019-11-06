package com.lothrazar.dimstack;

import net.minecraft.entity.player.EntityPlayer;
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

  private BlockPos position;

  public StackTeleporter(WorldServer world, BlockPos position) {
    super(world);
    this.position = position;
  }

  public void teleportToDimension(EntityPlayer player, int dimension, BlockPos pos) {
    BlockPos dimPos = pos;//getDimensionPosition((EntityPlayerMP) player, dimension, pos);
    teleportToDimension(player, dimension, dimPos.getX() + 0.5D, dimPos.getY(), dimPos.getZ() + 0.5D);
  }

  public void teleportToDimension(EntityPlayer player, int dimension, double x, double y, double z) {
    int oldDimension = player.getEntityWorld().provider.getDimension();
    EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
    this.world.playSound(null, x + 0.5D, y + 0.5D, z + 0.5D, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.25F, this.world.rand.nextFloat() * 0.4F + 0.8F);
    if (!player.capabilities.isCreativeMode) {
      player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 200, false, false));
    }
    if (this.world != null && this.world.getMinecraftServer() != null) {
      PlayerList playerList = this.world.getMinecraftServer().getPlayerList();
      playerList.transferPlayerToDimension(entityPlayerMP, dimension, this);
      player.setPositionAndUpdate(x, y, z);
      this.protectPlayer(entityPlayerMP, position);
    }
    else {
      throw new IllegalArgumentException("Dimension: " + dimension + " doesn't exist!");
    }
  }

  //thanks to mrbysco MIT 
  //https://github.com/Mrbysco/TelePastries/blob/044fd1b78a43cca9fbbc7a4bf07599dcde1d0c7c/src/main/java/com/mrbysco/telepastries/util/CakeTeleporter.java
  private void protectPlayer(EntityPlayerMP playerIn, BlockPos position) {
    boolean foundSuitablePlatform = false;
    if (this.world.provider.getDimension() != 0) {
      for (int j1 = 0; j1 < 5; j1++) {
        BlockPos checkingPos = position.add(0, -(j1), 0);
        /** Check to see if the block is solid. */
        if (this.world.getBlockState(checkingPos).isFullBlock()) {
          /** If there are solid blocks within a 3 block radius under you set foundSuitablePlatform to true */
          foundSuitablePlatform = true;
          break;
        }
      }
      for (int x = -2; x <= 2; x++) {
        for (int z = -2; z <= 2; z++) {
          if ((x == -2 || x == 2) && (z == -2 || z == 2)) {
            BlockPos testPos = new BlockPos(position.add(x, 3, z));
            if (!this.world.getBlockState(testPos).isFullBlock() && this.world.getBlockState(testPos).getMaterial().isLiquid()) {
              this.world.setBlockState(testPos, Blocks.DIRT.getDefaultState());
            }
          }
          else {
            if (this.world.getBlockState(position.add(x, 3, z)).getMaterial().isLiquid()) {
              this.world.setBlockState(position.add(x, 3, z), Blocks.DIRT.getDefaultState());
            }
            if (!foundSuitablePlatform) {
              BlockPos testPos = new BlockPos(position.add(x, -2, z));
              if (!this.world.getBlockState(testPos).isFullBlock() || this.world.getBlockState(testPos).getMaterial().isLiquid()) {
                this.world.setBlockState(testPos, Blocks.DIRT.getDefaultState());
              }
            }
          }
        }
      }
    }
    BlockPos platformPos = new BlockPos(position.add(1, 2, 1));
    for (int y = 1; y <= 3; y++) {
      if (this.world.getBlockState(position.add(0, y, 0)).isFullBlock() || this.world.getBlockState(position.add(0, y, 0)).getMaterial().isLiquid()) {
        for (int x = -1; x <= 1; x++) {
          for (int z = -1; z <= 1; z++) {
            BlockPos testPos = position.add(x, y, z);
            if (this.world.getBlockState(testPos).isFullBlock() || this.world.getBlockState(testPos).getMaterial().isLiquid()) {
              this.world.setBlockToAir(testPos);
            }
          }
        }
      }
    }
    playerIn.setLocationAndAngles(position.getX() + 0.5D, platformPos.getY(), position.getZ() + 0.5D, 90.0F, 0.0F);
    playerIn.setPositionAndUpdate(position.getX() + 0.5D, platformPos.getY(), position.getZ() + 0.5D);
  }
}
