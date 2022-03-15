package com.alspal.infinitetescos.common;

import com.alspal.infinitetescos.InfiniteTescosMod;
import com.mojang.math.Vector3d;

import net.minecraft.block.IWaterLoggable;
import net.minecraft.core.BlockPos;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShelfBlock extends Block implements IWaterLoggable
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
	
	private static final VoxelShape BASE = Block.makeCuboidShape(BASE_CORNER_MIN.getX(), BASE_CORNER_MIN.getX(), BASE_CORNER_MIN.getX(),
			BASE_CORNER_MAX.getX(), BASE_CORNER_MAX.getY(), BASE_CORNER_MAX.getZ());
	
	private static final VoxelShape[] WALLS = {
			//south wall
			Block.makeCuboidShape(WALL_SOUTH_MIN.getX(), WALL_SOUTH_MIN.getY(), WALL_SOUTH_MIN.getZ(),
					WALL_SOUTH_MAX.getX(), WALL_SOUTH_MAX.getY(), WALL_SOUTH_MAX.getZ()),
			//west wall
			Block.makeCuboidShape(WALL_WEST_MIN.getX(), WALL_WEST_MIN.getY(), WALL_WEST_MIN.getZ(),
					WALL_WEST_MAX.getX(), WALL_WEST_MAX.getY(), WALL_WEST_MAX.getZ()),
			//north wall
			Block.makeCuboidShape(WALL_NORTH_MIN.getX(), WALL_NORTH_MIN.getY(), WALL_NORTH_MIN.getZ(),
					WALL_NORTH_MAX.getX(), WALL_NORTH_MAX.getY(), WALL_NORTH_MAX.getZ()),
			//east wall
			Block.makeCuboidShape(WALL_EAST_MIN.getX(), WALL_EAST_MIN.getY(), WALL_EAST_MIN.getZ(),
					WALL_EAST_MAX.getX(), WALL_EAST_MAX.getY(), WALL_EAST_MAX.getZ())
	};
	
	private static final VoxelShape[] PILLARS = {
			//north west pillar
			Block.makeCuboidShape(PILLAR_NW_MIN.getX(), PILLAR_NW_MIN.getY(), PILLAR_NW_MIN.getZ(),
					PILLAR_NW_MAX.getX(), PILLAR_NW_MAX.getY(), PILLAR_NW_MAX.getZ()),
			
			//south west pillar
			Block.makeCuboidShape(PILLAR_SW_MIN.getX(), PILLAR_SW_MIN.getY(), PILLAR_SW_MIN.getZ(),
					PILLAR_SW_MAX.getX(), PILLAR_SW_MAX.getY(), PILLAR_SW_MAX.getZ()),
			
			//north west pillar
			Block.makeCuboidShape(PILLAR_SE_MIN.getX(), PILLAR_SE_MIN.getY(), PILLAR_SE_MIN.getZ(),
					PILLAR_SE_MAX.getX(), PILLAR_SE_MAX.getY(), PILLAR_SE_MAX.getZ()),
			
			//north east pillar
			Block.makeCuboidShape(PILLAR_NE_MIN.getX(), PILLAR_NE_MIN.getY(), PILLAR_NE_MIN.getZ(),
					PILLAR_NE_MAX.getX(), PILLAR_NE_MAX.getY(), PILLAR_NE_MAX.getZ()),
	};
	
	//straight shelf shapes
	private static VoxelShape[] STRAIGHT = {VoxelShapes.or(BASE, WALLS[0]), //south
		VoxelShapes.or(BASE, WALLS[1]), //west
		VoxelShapes.or(BASE, WALLS[2]), //north
		VoxelShapes.or(BASE, WALLS[3]), //east
	};
	
	static final Property<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
	static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
	static final Property<Boolean> WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public ShelfBlock()
	{
		super(Block.Properties.create(Material.IRON));
		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, Direction.SOUTH)
				.with(SHAPE, StairsShape.STRAIGHT)
				.with(WATERLOGGED, false));
	}
	
	@Override
	public VoxelShape getShape(BlockState blockstate, IBlockReader worldin, BlockPos pos, ISelectionContext context)
	{
		Direction direction = blockstate.get(FACING);
		BlockState infront = worldin.getBlockState(pos.offset(direction.getOpposite()));
		BlockState behind = worldin.getBlockState(pos.offset(direction));
		
		int currenthindex = direction.getHorizontalIndex();
		
		if(infront.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf")) //if infront is a shelf
				&& (Math.abs(currenthindex - infront.get(FACING).getHorizontalIndex()) == 1 || Math.abs(currenthindex - infront.get(FACING).getHorizontalIndex()) == 3)) //direction difference can be 1 or 3 (i.e. at 90 degrees to each other)
			return VoxelShapes.or(STRAIGHT[currenthindex], WALLS[infront.get(FACING).getHorizontalIndex()]); //return a corner shape of the orientation of the block and the one in front
		else if(behind.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf")) //if behind is a shelf
				&& (Math.abs(currenthindex - behind.get(FACING).getHorizontalIndex()) == 1 || Math.abs(currenthindex - behind.get(FACING).getHorizontalIndex()) == 3)) //direction difference can be 1 or 3 (i.e. at 90 degrees to each other))
		{
			//either southeast or northwest pillar
			if(currenthindex + behind.get(FACING).getHorizontalIndex() == 3)
			{
				//if either has a south component, it must be a southeast pillar
				if(currenthindex == 0 || behind.get(FACING).getHorizontalIndex() == 0)
					return VoxelShapes.or(BASE, PILLARS[2]); //return southeast pillar
				else //must be a northwest pillar
					return VoxelShapes.or(BASE, PILLARS[0]);
			}
			else if(currenthindex + behind.get(FACING).getHorizontalIndex() == 1) //must be a southwest pillar
				return VoxelShapes.or(BASE, PILLARS[1]);
			else
				return VoxelShapes.or(BASE, PILLARS[3]); //must be a northeast pillar
		}
		
		return STRAIGHT[currenthindex]; //array element contains a shape matching the horizontalindex of the direction
	}
	
	@Override
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(FACING, SHAPE, WATERLOGGED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		BlockState state = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing());
		
		FluidState flevel = context.getWorld().getFluidState(context.getPos());
		return state.with(SHAPE, getShelfShape(state, context.getWorld(), context.getPos()))
				.with(WATERLOGGED, flevel.getFluid() == Fluids.WATER);
	}
	
	@Override
	public BlockState updatePostPlacement(BlockState statein, Direction facing, BlockState facingstate, IWorld worldin, BlockPos currentpos, BlockPos facingpos)
	{
		if(statein.get(WATERLOGGED))
			worldin.getPendingFluidTicks().scheduleTick(currentpos, Fluids.WATER, Fluids.WATER.getTickRate(worldin));
		
		return statein.with(SHAPE, getShelfShape(statein, worldin, currentpos));
	}
	
	public StairsShape getShelfShape(BlockState state, IBlockReader worldIn, BlockPos pos)
	{
		Direction direction = state.get(FACING);
		BlockState infront = worldIn.getBlockState(pos.offset(direction.getOpposite()));
		BlockState behind = worldIn.getBlockState(pos.offset(direction));
		
		if(infront.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf"))
				&& direction.getAxis() != infront.get(FACING).getAxis()) //if infront is a shelf that is at 90 degrees
		{
			if(direction == infront.get(FACING).rotateYCCW())
				return StairsShape.INNER_LEFT;
			
			return StairsShape.INNER_RIGHT;
		}
		else if(behind.getBlock().getRegistryName().equals(new ResourceLocation(InfiniteTescosMod.MODID, "shelf"))
				&& direction.getAxis() != behind.get(FACING).getAxis()) //if behind is a shelf)
		{
			if(direction == behind.get(FACING).rotateYCCW())
				return StairsShape.OUTER_LEFT;
			
			return StairsShape.OUTER_RIGHT;
		}
		
		return StairsShape.STRAIGHT;
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
	}
	
}
