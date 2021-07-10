package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.blocks.states.VerticalConnection;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class DyeableVerticalConnectBlock extends DyeableBlock {
	public enum ConnectionType {
		DOUBLE, TRIPLE, PILLAR;
	}

	public static final EnumProperty<VerticalConnection> CONNECTION  = ModStateProperties.VERTICAL_CONNECTION;

	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> s) {
		s.add(COLOR, CONNECTION);
	}

	public final ConnectionType type;
	public final boolean connectOthers;

	public DyeableVerticalConnectBlock(Properties settings) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOthers = false;
	}

	public DyeableVerticalConnectBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings);
		type = tp;
		connectOthers = co;
	}

	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		World blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		BlockPos blockPosD = blockPos.below();
		//BlockPos blockPosU = blockPos.up();
		BlockState stateD = blockView.getBlockState(blockPosD);
		//BlockState stateU = blockView.getBlockState(blockPosU);
		
		//System.out.println("BlockPlaced!");
		if (stateD.getBlock() instanceof DyeableVerticalConnectBlock && (connectOthers || stateD.getBlock() == this)) {
			//System.out.println(stateD.get(CONNECTION));
			switch (stateD.getValue(CONNECTION)) {
			case S0:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.D1);
			case D0:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.D1);
			case T0:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.D1);
			case D1:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, type == ConnectionType.DOUBLE ? VerticalConnection.S0 : VerticalConnection.T2);
			case T1:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.T2);
			case T2:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, type == ConnectionType.PILLAR ? VerticalConnection.T2 : VerticalConnection.S0);
			default:
				return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.T2);
			}
		}
		return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.S0);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState,
			IWorld world, BlockPos pos, BlockPos posFrom) {
		BlockState res = state;
		
		if (direction == Direction.UP && newState.getBlock() instanceof DyeableVerticalConnectBlock && (connectOthers || newState.getBlock() == this)) {
			BlockState stateD = world.getBlockState(pos.below());
			switch (newState.getValue(CONNECTION)) {
			case D1:
				return res.setValue(CONNECTION, VerticalConnection.D0);
			case T1:
				return res.setValue(CONNECTION, (type == ConnectionType.PILLAR && stateD.getBlock() instanceof DyeableVerticalConnectBlock && (connectOthers || stateD.getBlock() == this)) ? VerticalConnection.T1 : VerticalConnection.T0);
			case T2:
				return res.setValue(CONNECTION, VerticalConnection.T1);
			default:
				break;
			}
		}
		return res;
	}
}
