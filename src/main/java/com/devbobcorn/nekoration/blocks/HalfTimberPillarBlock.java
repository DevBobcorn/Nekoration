package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.blocks.states.VerticalConnection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class HalfTimberPillarBlock extends HalfTimberBlock {
	public enum ConnectionType {
		DOUBLE, TRIPLE, PILLAR;
	}

	public static final EnumProperty<VerticalConnection> CONNECTION  = ModStateProperties.VERTICAL_CONNECTION;

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(COLOR0, COLOR1, CONNECTION);
	}

	public final ConnectionType type;
	public final boolean connectOthers;

	public HalfTimberPillarBlock(Properties settings) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOthers = false;
	}

	public HalfTimberPillarBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings);
		type = tp;
		connectOthers = co;
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		BlockPos blockPosD = blockPos.below();
		BlockState stateD = blockView.getBlockState(blockPosD);
		
		//System.out.println("BlockPlaced!");
		if (stateD.getBlock() instanceof HalfTimberPillarBlock && (connectOthers || stateD.getBlock() == this)) {
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
			LevelAccessor world, BlockPos pos, BlockPos posFrom) {
		BlockState res = state;
		
		if (direction == Direction.UP && newState.getBlock() instanceof HalfTimberPillarBlock && (connectOthers || newState.getBlock() == this)) {
			BlockState stateD = world.getBlockState(pos.below());
			switch (newState.getValue(CONNECTION)) {
			case D1:
				return res.setValue(CONNECTION, VerticalConnection.D0);
			case T1:
				return res.setValue(CONNECTION, (type == ConnectionType.PILLAR && stateD.getBlock() instanceof HalfTimberPillarBlock && (connectOthers || stateD.getBlock() == this)) ? VerticalConnection.T1 : VerticalConnection.T0);
			case T2:
				return res.setValue(CONNECTION, VerticalConnection.T1);
			default:
				break;
			}
		}
		return res;
	}
}
