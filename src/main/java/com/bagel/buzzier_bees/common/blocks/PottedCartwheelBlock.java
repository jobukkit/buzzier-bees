package com.bagel.buzzier_bees.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

//Use the forge event that fires when a player right-clicks a block
//if the block is a flower pot and the player is holding a cartwheel, then set the blockstate to the correct state
public class PottedCartwheelBlock extends FlowerPotBlock {
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	
	@SuppressWarnings("deprecation")
	public PottedCartwheelBlock(Block flower, Properties properties) {
		super(flower, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
	      return SHAPE;
	   }
	
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
	      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	   }
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
