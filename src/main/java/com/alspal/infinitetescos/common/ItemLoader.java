package com.alspal.infinitetescos.common;

import net.minecraft.item.ItemGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ItemLoader
{
	public static BlockItem shelfitem,
		flooritem;
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent)
	{		
		shelfitem = new BlockItem(BlockLoader.shelf, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		shelfitem.setRegistryName(BlockLoader.shelf.getRegistryName());
		
		flooritem = new BlockItem(BlockLoader.floor, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS));
		flooritem.setRegistryName(BlockLoader.floor.getRegistryName());
		
		itemRegistryEvent.getRegistry().registerAll(shelfitem,
				flooritem);
	}
}
