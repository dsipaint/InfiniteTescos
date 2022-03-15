package com.alspal.infinitetescos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("infinitetescos")
public class InfiniteTescosMod
{
	public static final String MODID = "infinitetescos";

	/*
	 * TODO:
	 * 	add proper lore to the github and the mods.toml mod description
	 * 	add shelf edges to shelves, but edges of adjacent shelves don't exist
	 * 	make shelves waterloggable
	 * 
	 * change shelf logic back to instanceof
	 */
	
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static IEventBus MOD_EVENT_BUS;

    public InfiniteTescosMod()
    {
    	MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	//common events
    	MOD_EVENT_BUS.register(com.alspal.infinitetescos.common.BlockLoader.class);
    	MOD_EVENT_BUS.register(com.alspal.infinitetescos.common.ItemLoader.class);
    }
}
