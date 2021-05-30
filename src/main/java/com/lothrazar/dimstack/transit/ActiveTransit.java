package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.UtilWorld;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PortalInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

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

  @Override
  public PortalInfo getPortalInfo(Entity entity, ServerWorld destWorld, Function<ServerWorld, PortalInfo> defaultPortalInfo) {
    //  PortalInfo pos;
    //player.setPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D);
    //
    return new PortalInfo(new Vector3d(target.getX(), target.getY(), target.getZ()),
        Vector3d.ZERO, entity.rotationYaw, entity.rotationPitch);
  }

  public void teleport(PlayerEntity player) {
    if (transit.isRelative()) {
      portalPos = createOrFindPortal();
      DimstackMod.LOGGER.info(portalPos + " found from relative transit" + transit);
      if (source.goesUpwards()) {
        target = portalPos.add(1, 1, 1);
        teleportInternal(player);
      }
      else {
        target = portalPos.down(3);
        teleportInternal(player);
      }
      TranslationTextComponent to = new TranslationTextComponent("dimstack." + transit.getTargetDim().getPath() + ".name");
      player.sendMessage(new TranslationTextComponent("dimstack.teleport.info", to), player.getUniqueID());
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
          for (int y = 1; y < 4; y++) {
            world.setBlockState(portalPos.add(x, y, z), Blocks.AIR.getDefaultState(), 2);
          }
        }
      }
      TranslationTextComponent to = new TranslationTextComponent("dimstack." + transit.getTargetDim().getPath() + ".name");
      player.sendMessage(new TranslationTextComponent("dimstack.teleport.info", to), player.getUniqueID());
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
      ServerWorld dim = world.getServer().getWorld(UtilWorld.stringToDimension(transit.getTargetDim()));
      //      DimensionType dim = DimensionType.getById(transit.getTargetDim());
      //      player.changeDimension(server, teleporter)
      player.changeDimension(dim, this);
      this.world.playSound(null, target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_TRAVEL, SoundCategory.MASTER, 0.25F,
          this.world.rand.nextFloat() * 0.4F + 0.8F);
    }
    else {
      throw new IllegalArgumentException("Dimension: " + transit.getTargetDim() + " doesn't exist.");
    }
  }

  @Override
  public Entity placeEntity(Entity newEntity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
    newEntity.fallDistance = 0;
    //
    //    newEntity.setPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D);
    //    newEntity.moveToBlockPosAndAngles(target, yaw, newEntity.rotationPitch);
    DimstackMod.LOGGER.info(" placeEntity " + target);
    return repositionEntity.apply(false); //Must be false or we fall on vanilla. thanks /Mrbysco/TelePastries/
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
      BlockPos dest = new BlockPos(x, y, z);
      DimstackMod.LOGGER.info("portal starting at " + dest);
      if (source.goesUpwards()) { //We just went up, so search upwards.
        while (true) {
          dest = dest.up(); // dest.setPos(x, y + 1, z)
          if (y >= 255 || world.getBlockState(dest).getBlock() != Blocks.BEDROCK) {
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
          dest = dest.down(); // dest.setPos(x, --y, z)
          state = world.getBlockState(dest);
          y--;
        }
      }
      destination = dest.toImmutable();
    }
    else {
      destination = source.getTarget();
    }
    BlockState dest = world.getBlockState(destination);
    if (dest.getBlock() != DimstackRegistry.PORTAL.get()) {
      world.setBlockState(destination, DimstackRegistry.PORTAL.get().getDefaultState(), 2);
      PortalTile tile = (PortalTile) world.getTileEntity(destination);
      tile.setGoesUpwards(!source.goesUpwards());
      tile.setTarget(source.getPos());
      source.setTarget(tile.getPos());
      TransitUtil.buildStructure(tile);
    }
    return destination;
  }
}
