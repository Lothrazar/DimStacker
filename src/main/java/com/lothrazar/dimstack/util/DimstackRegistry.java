package com.lothrazar.dimstack.util;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.block.PortalBlock;
import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.item.KeyItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DimstackRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DimstackMod.MODID);
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DimstackMod.MODID);
  public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, DimstackMod.MODID);
  //
  //  
  //new:each key is a TO key
  //nether_key goes TO nether. config says where from and other transits. ie from overworld to nether
  //end_key goes TO end. config says all ie overworld to end.
  //each dimension gets exactly one key
  //twilightforest:twilightforest
  //forge tps
  public static final RegistryObject<Item> OVERWORLD_KEY = ITEMS.register("overworld_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), "minecraft:overworld"));
  public static final RegistryObject<Item> NETHER_KEY = ITEMS.register("nether_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), "minecraft:the_nether"));
  public static final RegistryObject<Item> END_KEY = ITEMS.register("end_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC), "minecraft:the_end"));
  //TODO: twilight_key -> dimension id IN CONFIG
  public static final RegistryObject<Item> TWILIGHT_KEY = ITEMS.register("twilight_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)
      , "twilightforest:twilightforest"));
  public static final RegistryObject<Item> LOSTCITIES_KEY = ITEMS.register("lostcities_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)
      , "lostcities:lostcity"));
  public static final RegistryObject<Item> GAIA_KEY = ITEMS.register("gaia_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)
      , "gaiadimension:gaia_dimension"));
  public static final RegistryObject<Item> UNDERGARDEN_KEY = ITEMS.register("undergarden_key", () -> new KeyItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)
      , "undergarden:undergarden"));
  //portal block: again is coded and colored to its from (current) dimension
  //portal block config is only dimension -> color. use same transport config
  public static final RegistryObject<Block> PORTAL = BLOCKS.register("portal", () -> new PortalBlock(Block.Properties.of(Material.PORTAL)));
  public static final RegistryObject<Item> PORTAL_I = ITEMS.register("portal", () -> new BlockItem(PORTAL.get(),
      new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
  public static final RegistryObject<BlockEntityType<PortalTile>> PORTAL_TILE = TILE_ENTITIES.register("portal", () ->
      BlockEntityType.Builder.of(PortalTile::new, PORTAL.get()).build(null));
}
