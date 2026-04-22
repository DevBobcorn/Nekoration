package io.devbobcorn.nekoration.blocks.containers;

import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.NekoConfig.VerConnectionDir;
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

        VerConnectionDir config = NekoConfig.VER_CONNECTION_DIR.get();
        if (config == VerConnectionDir.BOTH || config == VerConnectionDir.TOP2BOTTOM) {
            BlockState above = ctx.getLevel().getBlockState(ctx.getClickedPos().above());
            return placed.setValue(BOTTOM, above.getBlock() instanceof CupboardBlock);
        }
        return placed.setValue(BOTTOM, false);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        VerConnectionDir config = NekoConfig.VER_CONNECTION_DIR.get();
        if ((config == VerConnectionDir.BOTH || config == VerConnectionDir.BOTTOM2TOP) && direction == Direction.UP) {
            return state.setValue(BOTTOM, neighborState.getBlock() instanceof CupboardBlock);
        }
        return state;
    }
}
