package com.lothrazar.dimstack.util;

import com.lothrazar.dimstack.DimstackMod;
import com.lothrazar.dimstack.block.PortalBlock;
import com.lothrazar.dimstack.block.PortalTile;
import com.lothrazar.dimstack.item.KeyItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class DimstackRegistry {

  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DimstackMod.MODID);
  public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DimstackMod.MODID);
  public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DimstackMod.MODID);
  //
  //
  public static final RegistryObject<Item> KEY = ITEMS.register("key", () -> new KeyItem(new Item.Properties().group(ItemGroup.MISC)));
  public static final RegistryObject<Block> PORTAL = BLOCKS.register("portal", () -> new PortalBlock(Block.Properties.create(Material.PORTAL)));
  public static final RegistryObject<Item> PORTAL_I = ITEMS.register("portal", () -> new BlockItem(PORTAL.get(), new Item.Properties().group(ItemGroup.MISC)));
  public static final RegistryObject<TileEntityType<PortalTile>> PORTAL_TILE = TILE_ENTITIES.register("portal", () -> TileEntityType.Builder.create(() -> new PortalTile(), PORTAL.get()).build(null));
}
