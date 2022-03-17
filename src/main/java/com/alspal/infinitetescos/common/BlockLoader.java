package com.alspal.infinitetescos.common;

import com.alspal.infinitetescos.InfiniteTescosMod;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
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
		floor = new Block(Block.Properties.of(Material.STONE)).setRegistryName(InfiniteTescosMod.MODID, "floor");

		blockRegisterEvent.getRegistry().registerAll(shelf,
				floor);
	}
}
