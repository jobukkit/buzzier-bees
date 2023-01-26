package com.bagel.buzzier_bees.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.bagel.buzzier_bees.core.util.BlockStateUtils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("deprecation")
public class CandleBlock extends Block implements IWaterLoggable {
	public static final IntegerProperty CANDLES 	= BlockStateUtils.CANDLES_1_4;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final BooleanProperty LIT = BlockStateUtils.LIT;
	public static final DirectionProperty FACING 	= HorizontalBlock.HORIZONTAL_FACING;
	
	protected static final VoxelShape ONE_SHAPE 	= Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 9.0D, 10.0D);
	protected static final VoxelShape TWO_SHAPE 	= Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D);
	protected static final VoxelShape THREE_SHAPE 	= Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D);
	protected static final VoxelShape FOUR_SHAPE 	= Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 9.0D, 13.0D);
	
	public CandleBlock(Block.Properties properties) {
		super(properties);
    	this.setDefaultState(this.getDefaultState().with(CANDLES, 1).with(WATERLOGGED, true).with(LIT, true));
    }
	
	public int getLightValue(BlockState state) {
		return this.isInBadEnvironment(state) ? 0 : super.getLightValue(state) + (11 + (1 * state.get(CANDLES)));	
	}
	
	@Nullable
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState blockstate = context.getWorld().getBlockState(context.getPos());
		Direction direction = context.getPlacementHorizontalFacing();
		if (blockstate.getBlock() == this) {
			return blockstate.with(FACING, direction).with(CANDLES, Integer.valueOf(Math.min(4, blockstate.get(CANDLES) + 1)));
		} else {
			IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
			boolean flag = ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
			return this.getDefaultState().with(FACING, direction).with(WATERLOGGED, flag);
		}
	}
	
	/*
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		int candles = state.get(CANDLES);
		Direction facing = state.get(FACING);
		boolean waterlogged = state.get(WATERLOGGED);
		BlockState candleOutput = candleOutput(player.getHeldItem(handIn).getItem(), this);
		if (candleOutput != null && candleOutput.getBlock() != this) {
			if (!player.abilities.isCreativeMode) {
				player.getHeldItem(handIn).shrink(1);
			}
			worldIn.setBlockState(pos, candleOutput.with(CANDLES, candles).with(FACING, facing).with(WATERLOGGED, waterlogged));
			return ActionResultType.SUCCESS;	    
		} else {
			return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);	
		}	
	}
	
	public BlockState candleOutput(Item item, Block block) {
		if (item == ModItems.WAX.get()) { return ModBlocks.CANDLE.get().getDefaultState(); }
		if (item == Items.WHITE_DYE) { return ModBlocks.WHITE_CANDLE.get().getDefaultState(); }
		if (item == Items.ORANGE_DYE) { return ModBlocks.ORANGE_CANDLE.get().getDefaultState(); }
		if (item == Items.MAGENTA_DYE) { return ModBlocks.MAGENTA_CANDLE.get().getDefaultState(); }
		if (item == Items.LIGHT_BLUE_DYE) { return ModBlocks.LIGHT_BLUE_CANDLE.get().getDefaultState(); }
		if (item == Items.YELLOW_DYE) { return ModBlocks.YELLOW_CANDLE.get().getDefaultState(); }
		if (item == Items.LIME_DYE) { return ModBlocks.LIME_CANDLE.get().getDefaultState(); }
		if (item == Items.PINK_DYE) { return ModBlocks.PINK_CANDLE.get().getDefaultState(); }
		if (item == Items.GRAY_DYE) { return ModBlocks.GRAY_CANDLE.get().getDefaultState(); }
		if (item == Items.LIGHT_GRAY_DYE) { return ModBlocks.LIGHT_GRAY_CANDLE.get().getDefaultState(); }
		if (item == Items.CYAN_DYE) { return ModBlocks.CYAN_CANDLE.get().getDefaultState(); }
		if (item == Items.PURPLE_DYE) { return ModBlocks.PURPLE_CANDLE.get().getDefaultState(); }
		if (item == Items.BLUE_DYE) { return ModBlocks.BLUE_CANDLE.get().getDefaultState(); }
		if (item == Items.BROWN_DYE) { return ModBlocks.BROWN_CANDLE.get().getDefaultState(); }
		if (item == Items.GREEN_DYE) { return ModBlocks.GREEN_CANDLE.get().getDefaultState(); }
		if (item == Items.RED_DYE) { return ModBlocks.RED_CANDLE.get().getDefaultState(); }
		if (item == Items.BLACK_DYE) { return ModBlocks.BLACK_CANDLE.get().getDefaultState(); }
		return null;
	}*/
	
	private boolean isInBadEnvironment(BlockState state) {
		return state.get(WATERLOGGED);	
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return mirrorIn == Mirror.NONE ? state : state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos blockpos = pos.down();
	    return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
	 }
	
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return !state.getCollisionShape(worldIn, pos).project(Direction.UP).isEmpty();	
	}
	
	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos) {
		return (0.1F * state.get(CANDLES));	
	}

	public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (!state.isValidPosition(worldIn, currentPos)) {
			return Blocks.AIR.getDefaultState();
		} else {
			if (state.get(WATERLOGGED)) {
				worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));	
			}
			return super.updatePostPlacement(state, facing, facingState, worldIn, currentPos, facingPos);	
		}	
	}
	
	public boolean isReplaceable(BlockState state, BlockItemUseContext useContext) {
		return useContext.getItem().getItem() == this.asItem() && state.get(CANDLES) < 4 ? true : super.isReplaceable(state, useContext);
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch(state.get(CANDLES)) {
		case 1:
			default:
				return ONE_SHAPE;	
		case 2:
			return TWO_SHAPE;	
		case 3:
			return THREE_SHAPE;	
		case 4:
			return FOUR_SHAPE;	
		}	
	}
	
	public IFluidState getFluidState(BlockState state) {
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);	
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(CANDLES, WATERLOGGED, LIT, FACING);	
	}
	
	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}
	
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return type == PathType.AIR && !this.blocksMovement ? true : super.allowsMovement(state, worldIn, pos, type);	
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World worldIn, BlockPos pos, Random rand) {
		if(state.get(WATERLOGGED) == false && state.get(LIT)) {
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			
			if(state.get(CANDLES) == 1) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.5D, y+0.75D, z+0.5D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.5D, y+0.75D, z+0.5D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 2 && state.get(FACING) == Direction.NORTH) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.5625D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.5625D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.375D, y+0.625D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.375D, y+0.625D, z+0.625D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 2 && state.get(FACING) == Direction.EAST) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.75D, z+0.5625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.75D, z+0.5625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.375D, y+0.625D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.375D, y+0.625D, z+0.375D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 2 && state.get(FACING) == Direction.SOUTH) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.4375D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.4375D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.625D, y+0.625D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.625D, y+0.625D, z+0.375D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 2 && state.get(FACING) == Direction.WEST) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.75D, z+0.4375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.75D, z+0.4375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.625D, y+0.625D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.625D, y+0.625D, z+0.625D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 3 && state.get(FACING) == Direction.NORTH) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.75D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.75D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.5D, y+0.6875D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.5D, y+0.6875D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.625D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.625D, z+0.3125D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 3 && state.get(FACING) == Direction.EAST) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.625D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.625D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.6875D, z+0.5D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.6875D, z+0.5D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.625D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.625D, z+0.3125D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 3 && state.get(FACING) == Direction.SOUTH) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.75D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.75D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.5D, y+0.6875D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.5D, y+0.6875D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.625D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.625D, z+0.6875D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 3 && state.get(FACING) == Direction.WEST) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.375D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.375D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.6875D, z+0.5D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.6875D, z+0.5D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.625D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.625D, z+0.6875D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 4 && state.get(FACING) == Direction.NORTH) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.625D, y+0.6875D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.625D, y+0.6875D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.625D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.625D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.4375D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.4375D, z+0.6875D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 4 && state.get(FACING) == Direction.EAST) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.375D, y+0.6875D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.375D, y+0.6875D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.625D, y+0.625D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.625D, y+0.625D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.4375D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.4375D, z+0.3125D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 4 && state.get(FACING) == Direction.SOUTH) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.75D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.375D, y+0.6875D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.375D, y+0.6875D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.625D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.625D, z+0.625D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.4375D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.4375D, z+0.3125D, 0.002D, 0.01D, 0.002D);
			}
			if(state.get(CANDLES) == 4 && state.get(FACING) == Direction.WEST) {
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.3125D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.3125D, y+0.75D, z+0.3125D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.625D, y+0.6875D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.625D, y+0.6875D, z+0.375D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.375D, y+0.625D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.375D, y+0.625D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.SMOKE, x+0.6875D, y+0.4375D, z+0.6875D, 0.002D, 0.01D, 0.002D);
				worldIn.addParticle(ParticleTypes.FLAME, x+0.6875D, y+0.4375D, z+0.6875D, 0.002D, 0.01D, 0.002D);
			}
		}
	}
}
