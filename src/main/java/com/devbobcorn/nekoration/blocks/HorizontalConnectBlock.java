package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.blocks.states.HorizontalConnection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HorizontalConnectBlock extends HorizontalBlock {
	public enum ConnectionType {
		DOUBLE, TRIPLE, BEAM;
	}

	public static final EnumProperty<HorizontalConnection> CONNECTION  = ModStateProperties.HONRIZONTAL_CONNECTION;

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(CONNECTION, FACING);
	  }

	public final ConnectionType type;
	public final boolean connectOthers;

	public HorizontalConnectBlock(Properties settings) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOthers = false;
	}

	public HorizontalConnectBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings);
		type = tp;
		connectOthers = co;
	}

	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		BlockPos blockPosL = getLeftBlock(blockPos, ctx.getHorizontalDirection().getOpposite());
		BlockState stateL = blockView.getBlockState(blockPosL);
		
		//System.out.println("BlockPlaced!");
		if (stateL.getBlock() instanceof HorizontalConnectBlock && (connectOthers || stateL.getBlock() == this)) {
			//System.out.println(stateL.getValue(CONNECTION));
			switch (stateL.getValue(CONNECTION)) {
			case S0:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
			case D0:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
			case T0:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.D1);
			case D1:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T2);
			case T1:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T2);
			case T2:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, type == ConnectionType.BEAM ? HorizontalConnection.T2 : HorizontalConnection.S0);
			default:
				return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.T2);
			}
		}
		return super.getStateForPlacement(ctx).setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(CONNECTION, HorizontalConnection.S0);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState,
			IWorld world, BlockPos pos, BlockPos posFrom) {
		BlockState res = state;
		
		if (direction == getRightDir(state.getValue(FACING)) && newState.getBlock() instanceof HorizontalConnectBlock && (connectOthers || newState.getBlock() == this)) {
			BlockState stateL = world.getBlockState(getLeftBlock(pos, state.getValue(FACING)));
			switch (newState.getValue(CONNECTION)) {
			case D1:
				return res.setValue(CONNECTION, HorizontalConnection.D0);
			case T1:
				return res.setValue(CONNECTION, (type == ConnectionType.BEAM && stateL.getBlock() instanceof HorizontalConnectBlock && (connectOthers || stateL.getBlock() == this)) ? HorizontalConnection.T1 : HorizontalConnection.T0);
			case T2:
				return res.setValue(CONNECTION, HorizontalConnection.T1);
			default:
				break;
			}
		}
		return res;
	}

	public BlockPos getLeftBlock(BlockPos pos, Direction dir) {
		switch (dir) {
		case NORTH:
			return pos.east();
		case EAST:
			return pos.south();
		case SOUTH:
			return pos.west();
		default:
			return pos.north();
		}
	}
	
	public BlockPos getRightBlock(BlockPos pos, Direction dir) {
		switch (dir) {
		case NORTH:
			return pos.west();
		case EAST:
			return pos.north();
		case SOUTH:
			return pos.east();
		default:
			return pos.south();
		}
	}

	public Direction getLeftDir(Direction selfDir) {
		switch (selfDir) {
		case NORTH:
			return Direction.EAST;
		case EAST:
			return Direction.SOUTH;
		case SOUTH:
			return Direction.WEST;
		default:
			return Direction.NORTH;
		}
	}
	
	public Direction getRightDir(Direction selfDir) {
		switch (selfDir) {
		case NORTH:
			return Direction.WEST;
		case EAST:
			return Direction.NORTH;
		case SOUTH:
			return Direction.EAST;
		default:
			return Direction.SOUTH;
		}
	}
}
