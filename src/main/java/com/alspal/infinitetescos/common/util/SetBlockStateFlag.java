package com.alspal.infinitetescos.common.util;

public enum SetBlockStateFlag
{
	BLOCKUPDATE(1),
	SEND_TO_CLIENTS(2),
	DO_NOT_RENDER(4),
	RUN_RENDER_ON_MAIN_THREAD(8),
	PREVENT_NEIGHBOUR_INTERACTIONS(16),
	NEIGHBOUR_INTERACTIONS_DONT_SPAWN_DROPS(32),
	BLOCK_IS_BEING_MOVED(64);
	
	private int flagvalue;
	
	SetBlockStateFlag(int flagvalue)
	{
		this.flagvalue = flagvalue;
	}
	
	public static int get(SetBlockStateFlag... flags)
	{
		int result = 0;
		
		for(SetBlockStateFlag flag : flags)
			result |= flag.flagvalue;
		
		return result;
	}
}
