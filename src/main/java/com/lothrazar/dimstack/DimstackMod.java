package com.lothrazar.dimstack;

import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DimstackMod.MODID, updateJSON = "https://raw.githubusercontent.com/Lothrazar/DimStacker/master/update.json")
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
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(config);
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onItemTooltipEvent(ItemTooltipEvent event) {
    //
    if (config.doTooltips())
      for (PlayerTransmit t : config.emitters) {
        if (t.keyCached != null && t.keyCached == event.getItemStack().getItem()
            && t.keyMeta == event.getItemStack().getMetadata()) {
          //
          TextFormatting f = this.getTextColour(t, event.getEntityPlayer());
          event.getToolTip().add(f + lang(MODID + ".tooltip") + " [" + t.from + "," + t.to + "]");
          if (event.getFlags().isAdvanced()) {
            event.getToolTip().add(t.toString());
          }
        }
      }
  }

  @SideOnly(Side.CLIENT)
  public static String lang(String message) {
    return I18n.format(message);
  }

  private TextFormatting getTextColour(PlayerTransmit t, EntityPlayer player) {
    if (player == null || t == null || t.from != player.dimension) {
      return TextFormatting.GRAY;
    }
    int playerY = player.getPosition().getY();
    if (Math.abs(playerY - t.yLimit) < config.getRedDistance()) {
      return TextFormatting.DARK_RED;
    }
    if (Math.abs(playerY - t.yLimit) < config.getOrangeDistance()) {
      return TextFormatting.LIGHT_PURPLE;
    }
    return TextFormatting.GRAY;
  }

  @SubscribeEvent
  public void playerTick(PlayerEvent.LivingUpdateEvent event) {
    //
    if (event.getEntity() instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) event.getEntity();
      //ok 
      //      int dimCurrent = player.dimension;
      for (PlayerTransmit t : config.emitters) {
        if (t.keyCached == null && t.key != null) {
          Item key = Item.getByNameOrId(t.key);
          if (key == null) {
            logger.error("Invalid key from config " + t);
            continue;
          }
          t.keyCached = key;//save backup
        }
        if (t.key != null) {
          if (player.getHeldItemMainhand().getItem() != t.keyCached
              || player.getHeldItemMainhand().getMetadata() != t.keyMeta) {
            continue;
          }
        }
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

  public static void sendStatusMessage(EntityPlayer player, String string) {
    //    player.sendStatusMessage(new TextComponentTranslation(string), true);
    player.sendMessage(new TextComponentTranslation(string));
  }

  /***
   * relative or exact
   * 
   * @param player
   * @param t
   */
  private void goTeleport(EntityPlayer player, PlayerTransmit t) {
    World world = player.world;
    if (!player.isDead) {
      if (!world.isRemote && !player.isRiding() && !player.isBeingRidden() && player.isNonBoss()) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        WorldServer worldServer = playerMP.getServer().getWorld(t.to);
        if (worldServer == null) {
          logger.error("Invalid dimension from config " + t);
          return;
        }
        //        logger.info("TP to valid spawn: " + t);
        try {
          if (t.relative) {
            BlockPos target = player.getPosition();
            float x = target.getX() * t.ratio;
            float z = target.getX() * t.ratio;
            target = new BlockPos((int) x, t.pos.getY(), (int) z);
            StackTeleporter teleporter = new StackTeleporter(worldServer, target);
            teleporter.teleportToDimension(playerMP, t.to);
          }
          else if (t.pos != null) {
            StackTeleporter teleporter = new StackTeleporter(worldServer, t.pos);
            teleporter.teleportToDimension(playerMP, t.to);
          }
          if (this.config.doChatMessage()) {
            sendStatusMessage(playerMP, TextFormatting.BLACK + Integer.toHexString(t.hashCode()) +
                TextFormatting.WHITE + " rift activated");
          }
        }
        catch (Exception e) {
          logger.error("bad TP? ", e);
        }
      }
    }
  }
}
