package com.alspal.infinitetescos.common;

import com.alspal.infinitetescos.InfiniteTescosMod;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BlockLoader
{
	public static Block shelf,
		floor;
	
	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> blockRegisterEvent)
	{
		shelf = new ShelfBlock().setRegistryName(InfiniteTescosMod.MODID, "shelf");
		floor = new Block(Block.Properties.create(Material.ROCK)).setRegistryName(InfiniteTescosMod.MODID, "floor");
		
		blockRegisterEvent.getRegistry().registerAll(shelf,
				floor);
	}
}
