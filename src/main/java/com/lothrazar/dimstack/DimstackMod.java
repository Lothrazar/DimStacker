package com.lothrazar.dimstack;

import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = DimstackMod.MODID)
public class DimstackMod {

  public static final String MODID = "dimstack";
  static Logger logger;
  DimConfig config;

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    logger = event.getModLog();
    config = new DimConfig(new Configuration(event.getSuggestedConfigurationFile()));
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    // some example code
    logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(config);
  }

  @SubscribeEvent
  public void playerTick(PlayerEvent.LivingUpdateEvent event) {
    //
    if (event.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) event.getEntity();
      //ok 
      //      int dimCurrent = player.dimension;
      for (PlayerTransmit t : config.emitters) {
        Item key = Item.getByNameOrId(t.key);
        if (key == null) {
          logger.error("Invalid key from config " + t);
          continue;
        }
        if (player.getHeldItemMainhand().getItem() != key) {
          continue;
        }
        //
        //
        int playerY = player.getPosition().getY();
        if (t.from == player.dimension) {
          //ok maybve we go here
          if (t.greaterThan && playerY > t.yLimit) {
            //GO
            this.goTeleport(player, t);
          }
          else if (!t.greaterThan && playerY <= t.yLimit) {
            //GO
            this.goTeleport(player, t);
          }
        }
      }
    }
  }

  private void goTeleport(EntityPlayer player, PlayerTransmit t) {
    // TODO Auto-generated method stub
    World world = player.world;
    if (!player.isDead) {
      if (!world.isRemote && !player.isRiding() && !player.isBeingRidden() && player.isNonBoss()) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        WorldServer worldServer = playerMP.getServer().getWorld(t.to);
        if (worldServer == null) {
          logger.error("Invalid dimension from config " + t);
          return;
        }
        StackTeleporter teleporter = new StackTeleporter(worldServer, worldServer.getSpawnCoordinate());
        //          teleporter.addDimensionPosition(playerMP, playerMP.dimension, playerMP.getPosition().add(0,1,0));
        teleporter.teleportToDimension(playerMP, t.to, worldServer.getSpawnCoordinate());
        //
        if (t.setSpawnOnDestination) {
          player.setSpawnChunk(new BlockPos(player), true, worldServer.provider.getDimension());
        }
        //
      }
    }
  }
}
