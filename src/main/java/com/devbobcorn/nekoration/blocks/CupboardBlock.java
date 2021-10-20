package com.devbobcorn.nekoration.blocks;

import com.devbobcorn.nekoration.NekoConfig;
import com.devbobcorn.nekoration.NekoConfig.VerConnectionDir;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CupboardBlock extends ItemDisplayBlock {
    public static final BooleanProperty BOTTOM  = BlockStateProperties.BOTTOM;

    public CupboardBlock(Properties settings) {
        super(settings);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> s) {
		s.add(COLOR, FACING, OPEN, BOTTOM);
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		Level blockView = ctx.getLevel();
		BlockPos blockPos = ctx.getClickedPos();
		VerConnectionDir config = NekoConfig.SERVER.verConnectionDir.get();
		
		if (config == VerConnectionDir.BOTH || config == VerConnectionDir.TOP2BOTTOM){
			BlockPos blockPosRef = blockPos.above();
			BlockState stateRef = blockView.getBlockState(blockPosRef);

			if (stateRef.getBlock() instanceof CupboardBlock){
				return super.getStateForPlacement(ctx).setValue(BOTTOM, true);
			} else return super.getStateForPlacement(ctx).setValue(BOTTOM, false);
		}
		return super.getStateForPlacement(ctx).setValue(BOTTOM, false);
	}

	public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
		VerConnectionDir config = NekoConfig.SERVER.verConnectionDir.get();

		if (config == VerConnectionDir.BOTH || config == VerConnectionDir.BOTTOM2TOP)
			if (direction == Direction.UP)
				if (newState.getBlock() instanceof CupboardBlock)
					return state.setValue(BOTTOM, true);
				else return state.setValue(BOTTOM, false);
		return state;
	}
}
