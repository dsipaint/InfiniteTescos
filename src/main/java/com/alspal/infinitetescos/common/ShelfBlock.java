package com.alspal.infinitetescos.common;

import com.alspal.infinitetescos.InfiniteTescosMod;
import com.mojang.math.Vector3d;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShelfBlock extends Block implements SimpleWaterloggedBlock
{
	private static final Vector3d BASE_CORNER_MIN = new Vector3d(0, 0, 0);
	private static final Vector3d BASE_CORNER_MAX = new Vector3d(16, 1, 16);
	
	private static final Vector3d WALL_SOUTH_MIN = new Vector3d(16, 0, 16);
	private static final Vector3d WALL_SOUTH_MAX = new Vector3d(0, 16, 15);
	
	private static final Vector3d WALL_WEST_MIN = new Vector3d(0, 0, 0);
	private static final Vector3d WALL_WEST_MAX = new Vector3d(1, 16, 16);
	
	private static final Vector3d WALL_NORTH_MIN = new Vector3d(0, 0, 0);
	private static final Vector3d WALL_NORTH_MAX = new Vector3d(16, 16, 1);
	
	private static final Vector3d WALL_EAST_MIN = new Vector3d(16, 0, 0);
	private static final Vector3d WALL_EAST_MAX = new Vector3d(15, 16, 16);
	
	
	private static final Vector3d PILLAR_NW_MIN = new Vector3d(0, 0, 0);
	private static final Vector3d PILLAR_NW_MAX = new Vector3d(1, 16, 1);
	
	private static final Vector3d PILLAR_SW_MIN = new Vector3d(0, 0, 16);
	private static final Vector3d PILLAR_SW_MAX = new Vector3d(1, 16, 15);
	
	private static final Vector3d PILLAR_SE_MIN = new Vector3d(16, 0, 16);
	private static final Vector3d PILLAR_SE_MAX = new Vector3d(15, 16, 15);
	
	private static final Vector3d PILLAR_NE_MIN = new Vector3d(16, 0, 0);
	private static final Vector3d PILLAR_NE_MAX = new Vector3d(15, 16, 1);
	
	private static final VoxelShape BASE = Block.box(BASE_CORNER_MIN.x, BASE_CORNER_MIN.y, BASE_CORNER_MIN.z,
			BASE_CORNER_MAX.x, BASE_CORNER_MAX.y, BASE_CORNER_MAX.z);
	
	private static final VoxelShape[] WALLS = {
			//south wall
			Block.box(WALL_SOUTH_MIN.x, WALL_SOUTH_MIN.y, WALL_SOUTH_MIN.z,
					WALL_SOUTH_MAX.x, WALL_SOUTH_MAX.y, WALL_SOUTH_MAX.z),
			//west wall
			Block.box(WALL_WEST_MIN.x, WALL_WEST_MIN.y, WALL_WEST_MIN.z,
					WALL_WEST_MAX.x, WALL_WEST_MAX.y, WALL_WEST_MAX.z),
			//north wall
			Block.box(WALL_NORTH_MIN.x, WALL_NORTH_MIN.y, WALL_NORTH_MIN.z,
					WALL_NORTH_MAX.x, WALL_NORTH_MAX.y, WALL_NORTH_MAX.z),
			//east wall
			Block.box(WALL_EAST_MIN.x, WALL_EAST_MIN.y, WALL_EAST_MIN.z,
					WALL_EAST_MAX.x, WALL_EAST_MAX.y, WALL_EAST_MAX.z)
	};
	
	private static final VoxelShape[] PILLARS = {
			//north west pillar
			Block.box(PILLAR_NW_MIN.x, PILLAR_NW_MIN.y, PILLAR_NW_MIN.z,
					PILLAR_NW_MAX.x, PILLAR_NW_MAX.y, PILLAR_NW_MAX.z),
			
			//south west pillar
			Block.box(PILLAR_SW_MIN.x, PILLAR_SW_MIN.y, PILLAR_SW_MIN.z,
					PILLAR_SW_MAX.x, PILLAR_SW_MAX.y, PILLAR_SW_MAX.z),
			
			//north west pillar
			Block.box(PILLAR_SE_MIN.x, PILLAR_SE_MIN.y, PILLAR_SE_MIN.z,
					PILLAR_SE_MAX.x, PILLAR_SE_MAX.y, PILLAR_SE_MAX.z),
			
			//north east pillar
			Block.box(PILLAR_NE_MIN.x, PILLAR_NE_MIN.y, PILLAR_NE_MIN.z,
					PILLAR_NE_MAX.x, PILLAR_NE_MAX.y, PILLAR_NE_MAX.z),
	};
	
	//straight shelf shapes
	private static VoxelShape[] STRAIGHT = {Shapes.or(BASE, WALLS[0]), //south
		Shapes.or(BASE, WALLS[1]), //west
		Shapes.or(BASE, WALLS[2]), //north
		Shapes.or(BASE, WALLS[3]), //east
	};
	
	static final Property<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
	static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	static final Property<Boolean> WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public ShelfBlock()
	{
		super(Block.Properties.of(Material.METAL));
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH)
				.setValue(SHAPE, StairsShape.STRAIGHT)
				.setValue(WATERLOGGED, false));
	}
	
	@Override
	public VoxelShape getShape(BlockState blockstate, BlockGetter worldin, BlockPos pos, CollisionContext context)
	{
		Direction direction = blockstate.getValue(FACING);
		
		BlockState infront = null, behind = null;
		//if we're facing east, infront must be east and behind must be west
		if(direction == Direction.EAST)
		{
			infront = worldin.getBlockState(pos.east());
			behind = worldin.getBlockState(pos.west());
		}
		else if(direction == Direction.WEST)
		{
			infront = worldin.getBlockState(pos.west());
			behind = worldin.getBlockState(pos.east());
		}
		else if(direction == Direction.NORTH)
		{
			infront = worldin.getBlockState(pos.north());
			behind = worldin.getBlockState(pos.south());
		}
		else
		{
			infront = worldin.getBlockState(pos.south());
			infront = worldin.getBlockState(pos.north());
		}
		
		// int currenthindex = direction.getHorizontalIndex();
		int currenthindex = direction.get2DDataValue();
		
		// if(infront.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf")) //if infront is a shelf
		// 		&& (Math.abs(currenthindex - infront.getValue(FACING).getHorizontalIndex()) == 1 || Math.abs(currenthindex - infront.getValue(FACING).getHorizontalIndex()) == 3)) //direction difference can be 1 or 3 (i.e. at 90 degrees to each other)
		// 	return Shapes.or(STRAIGHT[currenthindex], WALLS[infront.getValue(FACING).getHorizontalIndex()]); //return a corner shape of the orientation of the block and the one in front
		// else if(behind.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf")) //if behind is a shelf
		// 		&& (Math.abs(currenthindex - behind.getValue(FACING).getHorizontalIndex()) == 1 || Math.abs(currenthindex - behind.getValue(FACING).getHorizontalIndex()) == 3)) //direction difference can be 1 or 3 (i.e. at 90 degrees to each other))

		if(infront.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf")) //if infront is a shelf
			&& (Math.abs(currenthindex - infront.getValue(FACING).get2DDataValue()) == 1 || Math.abs(currenthindex - infront.getValue(FACING).get2DDataValue()) == 3)) //direction difference can be 1 or 3 (i.e. at 90 degrees to each other)
			return Shapes.or(STRAIGHT[currenthindex], WALLS[infront.getValue(FACING).get2DDataValue()]); //return a corner shape of the orientation of the block and the one in front
		else if(behind.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf")) //if behind is a shelf
			&& (Math.abs(currenthindex - behind.getValue(FACING).get2DDataValue()) == 1 || Math.abs(currenthindex - behind.getValue(FACING).get2DDataValue()) == 3)) //direction difference can be 1 or 3 (i.e. at 90 degrees to each other))
		{
			// //either southeast or northwest pillar
			// if(currenthindex + behind.getValue(FACING).getHorizontalIndex() == 3)
			// {
			// 	//if either has a south component, it must be a southeast pillar
			// 	if(currenthindex == 0 || behind.getValue(FACING).getHorizontalIndex() == 0)
			// 		return Shapes.or(BASE, PILLARS[2]); //return southeast pillar
			// 	else //must be a northwest pillar
			// 		return Shapes.or(BASE, PILLARS[0]);
			// }
			// else if(currenthindex + behind.getValue(FACING).getHorizontalIndex() == 1) //must be a southwest pillar
			// 	return Shapes.or(BASE, PILLARS[1]);
			// else
			// 	return Shapes.or(BASE, PILLARS[3]); //must be a northeast pillar

			//either southeast or northwest pillar
			if(currenthindex + behind.getValue(FACING).get2DDataValue() == 3)
			{
				//if either has a south component, it must be a southeast pillar
				if(currenthindex == 0 || behind.getValue(FACING).get2DDataValue() == 0)
					return Shapes.or(BASE, PILLARS[2]); //return southeast pillar
				else //must be a northwest pillar
					return Shapes.or(BASE, PILLARS[0]);
			}
			else if(currenthindex + behind.getValue(FACING).get2DDataValue() == 1) //must be a southwest pillar
				return Shapes.or(BASE, PILLARS[1]);
			else
				return Shapes.or(BASE, PILLARS[3]); //must be a northeast pillar
		}
		
		return STRAIGHT[currenthindex]; //array element contains a shape matching the horizontalindex of the direction
	}
	
	@Override
	public void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING, SHAPE, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
		
		FluidState flevel = context.getLevel().getFluidState(context.getClickedPos());
		return state.setValue(SHAPE, getShelfShape(state, context.getLevel(), context.getClickedPos()))
				.setValue(WATERLOGGED, flevel.getType() == Fluids.WATER);
	}
	
	@Override
	public BlockState updateShape(BlockState statein, Direction facing, BlockState facingstate, LevelAccessor worldin, BlockPos currentpos, BlockPos facingpos)
	{
		if(statein.getValue(WATERLOGGED))
			worldin.scheduleTick(currentpos, Fluids.WATER, Fluids.WATER.getTickDelay(worldin));
		
		return statein.setValue(SHAPE, getShelfShape(statein, worldin, currentpos));
	}
	
	public StairsShape getShelfShape(BlockState state, BlockGetter worldIn, BlockPos pos)
	{
		Direction direction = state.getValue(FACING);
		
		BlockState infront = null, behind = null;
		//if we're facing east, infront must be east and behind must be west
		if(direction == Direction.EAST)
		{
			infront = worldIn.getBlockState(pos.east());
			behind = worldIn.getBlockState(pos.west());
		}
		else if(direction == Direction.WEST)
		{
			infront = worldIn.getBlockState(pos.west());
			behind = worldIn.getBlockState(pos.east());
		}
		else if(direction == Direction.NORTH)
		{
			infront = worldIn.getBlockState(pos.north());
			behind = worldIn.getBlockState(pos.south());
		}
		else
		{
			infront = worldIn.getBlockState(pos.south());
			infront = worldIn.getBlockState(pos.north());
		}
		
		if(infront.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf"))
				&& direction.getAxis() != infront.getValue(FACING).getAxis()) //if infront is a shelf that is at 90 degrees
		{
			if(direction == infront.getValue(FACING).getCounterClockWise())
				return StairsShape.INNER_LEFT;
			
			return StairsShape.INNER_RIGHT;
		}
		else if(behind.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf"))
				&& direction.getAxis() != behind.getValue(FACING).getAxis()) //if behind is a shelf)
		{
			if(direction == behind.getValue(FACING).getCounterClockWise())
				return StairsShape.OUTER_LEFT;
			
			return StairsShape.OUTER_RIGHT;
		}
		
		return StairsShape.STRAIGHT;
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
	}
	
}
