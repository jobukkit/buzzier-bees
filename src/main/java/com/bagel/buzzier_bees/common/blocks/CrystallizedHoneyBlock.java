package com.bagel.buzzier_bees.common.blocks;

import com.bagel.buzzier_bees.common.entities.HoneySlimeEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrystallizedHoneyBlock extends Block {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


	public CrystallizedHoneyBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)));
	}
	
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
	      boolean flag = worldIn.isBlockPowered(pos);
	      if (flag != state.get(POWERED)) {
	         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 3);
	         worldIn.destroyBlock(pos, true);
	      }

	   }
	
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if ( entityIn instanceof HoneySlimeEntity || entityIn instanceof ItemEntity ) {
		} else {
			worldIn.destroyBlock(pos, true);
		}	
	}
	
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		if ( entityIn instanceof HoneySlimeEntity || entityIn instanceof ItemEntity ) {
		} else {
			worldIn.destroyBlock(pos, true);
		}
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
		}
}
