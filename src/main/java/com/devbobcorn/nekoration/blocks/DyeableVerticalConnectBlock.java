package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.NekoConfig.VerConnectionDir;
import com.devbobcorn.nekoration.blocks.states.ModStateProperties;
import com.devbobcorn.nekoration.blocks.states.VerticalConnection;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class DyeableVerticalConnectBlock extends DyeableBlock {
	public enum ConnectionType {
		DOUBLE, TRIPLE, PILLAR;
	}

	public static final EnumProperty<VerticalConnection> CONNECTION  = ModStateProperties.VERTICAL_CONNECTION;

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(COLOR, CONNECTION);
	}

	public final ConnectionType type;
	public final boolean connectOtherVariant;

	public DyeableVerticalConnectBlock(Properties settings) {
		super(settings);
		type = ConnectionType.TRIPLE;
		connectOtherVariant = false;
	}

	public DyeableVerticalConnectBlock(Properties settings, ConnectionType tp, boolean co) {
		super(settings);
		type = tp;
		connectOtherVariant = co;
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		VerConnectionDir config = NekoConfig.SERVER.verConnectionDir.get();
		boolean useBottom = config == VerConnectionDir.BOTTOM2TOP || config == VerConnectionDir.BOTH;
		
		if (config != VerConnectionDir.NEITHER){
			BlockPos blockPosRef = (useBottom) ? blockPos.below() : blockPos.above();
			BlockState stateRef = blockView.getBlockState(blockPosRef);

			boolean connect = stateRef.getBlock() instanceof DyeableVerticalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this);

			if (!connect && config == VerConnectionDir.BOTH){ // Block below refuses to connect, try the above one
				blockPosRef = blockPos.above();
				stateRef = blockView.getBlockState(blockPosRef);
				connect = stateRef.getBlock() instanceof DyeableVerticalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this);
				useBottom = false;
			}
			if (connect) {
				if (useBottom){
					switch (stateRef.getValue(CONNECTION)) {
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
				} else {
					switch (stateRef.getValue(CONNECTION)) {
						case S0:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.D0);
						case D1:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.D0);
						case T2:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.D0);
						case D0:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, type == ConnectionType.DOUBLE ? VerticalConnection.S0 : VerticalConnection.T0);
						case T1:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.T0);
						case T0:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, type == ConnectionType.PILLAR ? VerticalConnection.T0 : VerticalConnection.S0);
						default:
							return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.T0);
					}
				}
			}
		}
		return super.getStateForPlacement(ctx).setValue(CONNECTION, VerticalConnection.S0);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
		BlockState res = state;
		VerConnectionDir config = NekoConfig.SERVER.verConnectionDir.get();
		boolean flag1 = direction == Direction.UP   && (config == VerConnectionDir.BOTTOM2TOP ||  config == VerConnectionDir.BOTH);
		boolean flag2 = direction == Direction.DOWN && (config == VerConnectionDir.TOP2BOTTOM ||  config == VerConnectionDir.BOTH);

		boolean connect = flag1 || flag2;
		if (connect && newState.getBlock() instanceof DyeableVerticalConnectBlock && (connectOtherVariant || newState.getBlock() == this)) {
			BlockState stateRef;
			if (flag1){ // Block above is ...
				stateRef = world.getBlockState(pos.below()); // Take the block below as an extra reference
				switch (newState.getValue(CONNECTION)) {
					case D1:
						return res.setValue(CONNECTION, VerticalConnection.D0);
					case T1:
						return res.setValue(CONNECTION, (type == ConnectionType.PILLAR && stateRef.getBlock() instanceof DyeableVerticalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this)) ? VerticalConnection.T1 : VerticalConnection.T0);
					case T2:
						return res.setValue(CONNECTION, VerticalConnection.T1);
					default:
						break;
				}
			} else { // Block below is ...
				stateRef = world.getBlockState(pos.above()); // Take the block above as an extra reference
				switch (newState.getValue(CONNECTION)) {
					case D0:
						return res.setValue(CONNECTION, VerticalConnection.D1);
					case T1:
						return res.setValue(CONNECTION, (type == ConnectionType.PILLAR && stateRef.getBlock() instanceof DyeableVerticalConnectBlock && (connectOtherVariant || stateRef.getBlock() == this)) ? VerticalConnection.T1 : VerticalConnection.T2);
					case T0:
						return res.setValue(CONNECTION, VerticalConnection.T1);
					default:
						break;
				}
			}
		}
		return res;
	}
}
