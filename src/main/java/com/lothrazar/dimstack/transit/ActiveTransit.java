package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.PortalTile;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class ActiveTransit implements ITeleporter {

  protected ServerWorld world;
  protected PortalTile source;
  protected Transit transit;
  protected BlockPos portalPos;
  protected BlockPos target;

  public ActiveTransit(ServerWorld world, PortalTile source, Transit transit) {
    this.world = world;
    this.source = source;
    this.transit = transit;
  }

  public void teleport(PlayerEntity player) {
    if (transit.isRelative()) {
      portalPos = createOrFindPortal();
      if (source.goesUpwards()) {
        target = portalPos.add(1, 1, 1);
        teleportInternal(player);
      }
      else {
        target = portalPos.down(3);
        teleportInternal(player);
      }
      TranslationTextComponent to = new TranslationTextComponent("dimstack." + DimensionType.getById(transit.getTargetDim()).getRegistryName() + ".name");
      player.sendMessage(new TranslationTextComponent("dimstack.teleport.info", to));
    }
    else {
      portalPos = transit.getTargetPos();
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          world.setBlockState(portalPos.add(x, 0, z), Blocks.STONE_BRICKS.getDefaultState(), 2);
        }
      }
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          for (int y = 1; y < 4; y++)
            world.setBlockState(portalPos.add(x, y, z), Blocks.AIR.getDefaultState(), 2);
        }
      }
      TranslationTextComponent to = new TranslationTextComponent("dimstack." + DimensionType.getById(transit.getTargetDim()).getRegistryName() + ".name");
      player.sendMessage(new TranslationTextComponent("dimstack.teleport.info", to));
      target = portalPos.up();
      teleportInternal(player);
    }
  }

  private void teleportInternal(PlayerEntity player) {
    if (!player.isCreative()) {
      player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 200, 200, false, false));
    }
    if (this.world != null && this.world.getServer() != null) {
      //needs to use DimensionType now
      DimensionType dim = DimensionType.getById(transit.getTargetDim());
      player.changeDimension(dim, this);
      this.world.playSound(null, target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.25F,
          this.world.rand.nextFloat() * 0.4F + 0.8F);
    }
    else {
      throw new IllegalArgumentException("Dimension: " + transit.getTargetDim() + " doesn't exist.");
    }
  }

  @Override
  public void placeEntity(World world, Entity entity, float yaw) {
    entity.moveToBlockPosAndAngles(target, yaw, entity.rotationPitch);
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
      }
      else { //We just went down, so search downwards.
        BlockState state = world.getBlockState(dest);
        while (true) {
          if (y <= 0 || state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
            break;
          }
          state = world.getBlockState(dest.setPos(x, --y, z));
        }
      }
      destination = dest.toImmutable();
    }
    else destination = source.getTarget();
    BlockState dest = world.getBlockState(destination);
    if (dest.getBlock() != DimstackMod.PORTAL) {
      world.setBlockState(destination, DimstackMod.PORTAL.getDefaultState(), 2);
      PortalTile tile = (PortalTile) world.getTileEntity(destination);
      tile.setGoesUpwards(!source.goesUpwards());
      tile.setTarget(source.getPos());
      source.setTarget(tile.getPos());
      TransitUtil.buildStructure(tile);
    }
    return destination;
  }
}
