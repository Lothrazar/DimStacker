package com.lothrazar.dimstack.block;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.transit.ActiveTransit;
import com.lothrazar.dimstack.transit.Transit;
import com.lothrazar.dimstack.transit.TransitManager;
import com.lothrazar.dimstack.util.DimstackRegistry;
import com.lothrazar.dimstack.util.UtilWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PortalBlock extends BaseEntityBlock {

  public static final VoxelShape PORTAL_AABB = Block.box(0, 0.4, 0, 1, 0.6, 1);

  public PortalBlock(Properties properties) {
    super(properties.strength(-1).lightLevel(state -> 15));
  }

  @Override
  public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return PORTAL_AABB;
  }

  @Override
  public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
    if (entity instanceof Player && entity.isAlive()) {
      if (!world.isClientSide && !entity.hasExactlyOnePlayerPassenger() && !entity.isVehicle() && entity.canChangeDimensions()) {
        Player playerMP = (Player) entity;
        if (playerMP.getCooldowns().isOnCooldown(DimstackRegistry.PORTAL_I.get())) {
          return;
        }
        PortalTile tile = (PortalTile) world.getBlockEntity(pos);
        Transit t = TransitManager.getTargetFor(tile);
        if (t == null || tile == null) {
          return;
        }
        //        playerMP.getServer().getw
        ServerLevel targetDim = playerMP.getServer().getLevel(UtilWorld.stringToDimension(t.getTargetDim()));
        try {
          ActiveTransit teleporter = new ActiveTransit(targetDim, tile, t);
          teleporter.teleport(playerMP);
          playerMP.getCooldowns().addCooldown(DimstackRegistry.PORTAL_I.get(), 20);
        }
        catch (Exception e) {
          DimstackMod.LOGGER.error("There has been an exception during an attempted teleportation.", e);
        }
      }
    }
  }

  @Override
  public RenderShape getRenderShape(BlockState bs) {
    return RenderShape.MODEL;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new PortalTile(pos, state);
  }

  @Override
  public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
    return 0;
  }
}
