package io.devbobcorn.nekoration.blocks.containers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Wooden cupboard block with vertical pair state.
 */
public class CupboardBlock extends ItemDisplayBlock {
    public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;

    public CupboardBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BOTTOM, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BOTTOM);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }

        if (ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
            return placed.setValue(BOTTOM, false);
        }

        BlockState above = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
        if (above.getBlock() instanceof CupboardBlock) {
            return placed.setValue(BOTTOM, true);
        }

        BlockState below = ctx.getLevel().getBlockState(ctx.getClickedPos().below());
        if (below.getBlock() instanceof CupboardBlock) {
            return placed.setValue(BOTTOM, false);
        }

        return placed.setValue(BOTTOM, false);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP) {
            return state.setValue(BOTTOM, neighborState.getBlock() instanceof CupboardBlock);
        }
        if (direction == Direction.DOWN && neighborState.getBlock() instanceof CupboardBlock) {
            return state.setValue(BOTTOM, false);
        }
        return state;
    }
}
