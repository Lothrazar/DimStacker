package com.lothrazar.dimstack.transit;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.TransitUtil;
import com.lothrazar.dimstack.util.UtilWorld;
import java.util.function.Function;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.ITeleporter;

public class ActiveTransit implements ITeleporter {

  protected ServerLevel world;
  protected PortalTile source;
  protected Transit transit;
  protected BlockPos portalPos;
  protected BlockPos target;

  public ActiveTransit(ServerLevel world, PortalTile source, Transit transit) {
    this.world = world;
    this.source = source;
    this.transit = transit;
  }

  @Override
  public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
    //  PortalInfo pos;
    //player.setPosition(target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D);
    //
    return new PortalInfo(new Vec3(target.getX(), target.getY(), target.getZ()),
        Vec3.ZERO, entity.getYRot(), entity.getXRot());
  }

  public void teleport(Player player) {
    if (transit.isRelative()) {
      portalPos = createOrFindPortal();
      DimstackMod.LOGGER.info(portalPos + " found from relative transit" + transit);
      if (portalPos == null) {
        return;
      }
      if (source.goesUpwards()) {
        target = portalPos.offset(1, 1, 1);
        teleportInternal(player);
      }
      else {
        target = portalPos.below(3);
        teleportInternal(player);
      }
      TranslatableComponent to = new TranslatableComponent(DimstackMod.MODID + "." + transit.getTargetDim().getPath());
      player.sendMessage(new TranslatableComponent("dimstack.teleport.info", to), player.getUUID());
      player.sendMessage(new TranslatableComponent(world.gatherChunkSourceStats()), player.getUUID());
    }
    else {
      portalPos = transit.getTargetPos();
      if (portalPos == null) {
        return;
      }
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          world.setBlock(portalPos.offset(x, 0, z), Blocks.STONE_BRICKS.defaultBlockState(), 2);
        }
      }
      for (int x = -1; x <= 1; x++) {
        for (int z = -1; z <= 1; z++) {
          for (int y = 1; y < 4; y++) {
            world.setBlock(portalPos.offset(x, y, z), Blocks.AIR.defaultBlockState(), 2);
          }
        }
      }
      TranslatableComponent to = new TranslatableComponent(DimstackMod.MODID + "." + transit.getTargetDim().getPath());
      player.sendMessage(new TranslatableComponent("dimstack.teleport.info", to), player.getUUID());
      target = portalPos.above();
      teleportInternal(player);
    }
  }

  private void teleportInternal(Player player) {
    if (!player.isCreative()) {
      player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 200, false, false));
    }
    if (this.world != null && this.world.getServer() != null) {
      //needs to use DimensionType now
      ServerLevel dim = world.getServer().getLevel(UtilWorld.stringToDimension(transit.getTargetDim()));
      //      DimensionType dim = DimensionType.getById(transit.getTargetDim());
      //      player.changeDimension(server, teleporter)
      player.changeDimension(dim, this);
      this.world.playSound(null, target.getX() + 0.5D, target.getY() + 0.5D, target.getZ() + 0.5D, SoundEvents.PORTAL_TRAVEL, SoundSource.MASTER, 0.25F,
          this.world.random.nextFloat() * 0.4F + 0.8F);
    }
    else {
      throw new IllegalArgumentException("Dimension: " + transit.getTargetDim() + " doesn't exist.");
    }
  }

  @Override
  public Entity placeEntity(Entity newEntity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
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
    if (world == null) {
      // for example: mod not installed for that dimension
      DimstackMod.LOGGER.error("Null world for rift " + this.transit);
      return null;
    }
    BlockPos destination;
    if (source.getTarget().equals(PortalTile.UNLINKED)) {
      BlockPos src = source.getBlockPos();
      int x = (int) (src.getX() * transit.getRatio());
      int y = transit.getLanding();
      int z = (int) (src.getZ() * transit.getRatio());
      BlockPos dest = new BlockPos(x, y, z);
      DimstackMod.LOGGER.info("portal starting at " + dest);
      if (source.goesUpwards()) { //We just went up, so search upwards.
        while (true) {
          dest = dest.above(); // dest.setPos(x, y + 1, z)
          if (y >= 255 || world.getBlockState(dest).getBlock() != Blocks.BEDROCK) {
            break;
          }
          y++;
        }
      }
      else { //We just went down, so search downwards.
        //todo : opposite transit?
        BlockState state = world.getBlockState(dest);
        while (true) {
          if (y <= 0 || state.getBlock() != Blocks.AIR && state.getBlock() != Blocks.BEDROCK) {
            break;
          }
          dest = dest.below(); // dest.setPos(x, --y, z)
          state = world.getBlockState(dest);
          y--;
        }
      }
      destination = dest.immutable();
    }
    else {
      destination = source.getTarget();
    }
    BlockState dest = world.getBlockState(destination);
    if (dest.getBlock() != DimstackRegistry.PORTAL.get()) {
      world.setBlock(destination, DimstackRegistry.PORTAL.get().defaultBlockState(), 2);
      PortalTile tile = (PortalTile) world.getBlockEntity(destination);
      Transit opposite = new Transit();
      opposite.from = transit.to;
      opposite.to = transit.from;
      opposite.goesUpwards = !source.goesUpwards(); //!transit.goesUpwards; 
      tile.setTransit(opposite);
      tile.setTarget(source.getBlockPos());
      source.setTarget(tile.getBlockPos());
      TransitUtil.buildStructure(tile);
    }
    return destination;
  }
}
