package io.devbobcorn.nekoration.blocks;

import io.devbobcorn.nekoration.NekoConfig;
import io.devbobcorn.nekoration.NekoConfig.VerConnectionDir;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import io.devbobcorn.nekoration.blocks.states.VerticalConnection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * Vertical stack connection without dye / plaster {@code color} (see {@link DyeableVerticalConnectBlock} and
 * {@link WindowBlock}).
 */
public class VerticalConnectBlock extends Block {
    public enum ConnectionType {
        DOUBLE, TRIPLE, PILLAR
    }

    public static final EnumProperty<VerticalConnection> CONNECTION = ModStateProperties.VERTICAL_CONNECTION;

    public final ConnectionType type;
    public final boolean connectOtherVariant;

    public VerticalConnectBlock(Properties settings) {
        super(settings);
        this.type = ConnectionType.TRIPLE;
        this.connectOtherVariant = false;
    }

    public VerticalConnectBlock(Properties settings, ConnectionType type, boolean connectOtherVariant) {
        super(settings);
        this.type = type;
        this.connectOtherVariant = connectOtherVariant;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONNECTION);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level level = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        VerConnectionDir config = NekoConfig.VER_CONNECTION_DIR.get();
        boolean useBottom = config == VerConnectionDir.BOTTOM2TOP || config == VerConnectionDir.BOTH;

        if (config != VerConnectionDir.NEITHER) {
            BlockPos blockPosRef = useBottom ? blockPos.below() : blockPos.above();
            BlockState stateRef = level.getBlockState(blockPosRef);

            boolean connect = stateRef.getBlock() instanceof VerticalConnectBlock
                    && (connectOtherVariant || stateRef.getBlock() == this);

            if (!connect && config == VerConnectionDir.BOTH) {
                blockPosRef = blockPos.above();
                stateRef = level.getBlockState(blockPosRef);
                connect = stateRef.getBlock() instanceof VerticalConnectBlock
                        && (connectOtherVariant || stateRef.getBlock() == this);
                useBottom = false;
            }
            if (connect) {
                if (useBottom) {
                    switch (stateRef.getValue(CONNECTION)) {
                        case S0, D0, T0 -> {
                            return defaultBlockState().setValue(CONNECTION, VerticalConnection.D1);
                        }
                        case D1 -> {
                            return defaultBlockState().setValue(CONNECTION,
                                    type == ConnectionType.DOUBLE ? VerticalConnection.S0 : VerticalConnection.T2);
                        }
                        case T1 -> {
                            return defaultBlockState().setValue(CONNECTION, VerticalConnection.T2);
                        }
                        case T2 -> {
                            return defaultBlockState().setValue(CONNECTION,
                                    type == ConnectionType.PILLAR ? VerticalConnection.T2 : VerticalConnection.S0);
                        }
                        default -> {
                            return defaultBlockState().setValue(CONNECTION, VerticalConnection.T2);
                        }
                    }
                } else {
                    switch (stateRef.getValue(CONNECTION)) {
                        case S0, D1, T2 -> {
                            return defaultBlockState().setValue(CONNECTION, VerticalConnection.D0);
                        }
                        case D0 -> {
                            return defaultBlockState().setValue(CONNECTION,
                                    type == ConnectionType.DOUBLE ? VerticalConnection.S0 : VerticalConnection.T0);
                        }
                        case T1 -> {
                            return defaultBlockState().setValue(CONNECTION, VerticalConnection.T0);
                        }
                        case T0 -> {
                            return defaultBlockState().setValue(CONNECTION,
                                    type == ConnectionType.PILLAR ? VerticalConnection.T0 : VerticalConnection.S0);
                        }
                        default -> {
                            return defaultBlockState().setValue(CONNECTION, VerticalConnection.T0);
                        }
                    }
                }
            }
        }
        return defaultBlockState().setValue(CONNECTION, VerticalConnection.S0);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        VerConnectionDir config = NekoConfig.VER_CONNECTION_DIR.get();
        boolean relevant = (direction == Direction.UP
                && (config == VerConnectionDir.BOTTOM2TOP || config == VerConnectionDir.BOTH))
                || (direction == Direction.DOWN
                && (config == VerConnectionDir.TOP2BOTTOM || config == VerConnectionDir.BOTH));

        if (relevant) {
            return recalculateConnection(state, level, pos);
        }
        return state;
    }

    private BlockState recalculateConnection(BlockState state, LevelAccessor level, BlockPos pos) {
        int blocksBelow = 0;
        BlockPos checkPos = pos.below();
        while (isConnectableNeighbor(level, checkPos)) {
            blocksBelow++;
            checkPos = checkPos.below();
        }

        int blocksAbove = 0;
        checkPos = pos.above();
        while (isConnectableNeighbor(level, checkPos)) {
            blocksAbove++;
            checkPos = checkPos.above();
        }

        int chainLength = blocksBelow + 1 + blocksAbove;
        int position = blocksBelow;

        return state.setValue(CONNECTION, getStateForChainPosition(chainLength, position));
    }

    private static VerticalConnection getStateForChainPosition(int chainLength, int position) {
        if (chainLength <= 1) {
            return VerticalConnection.S0;
        } else if (chainLength == 2) {
            return position == 0 ? VerticalConnection.D0 : VerticalConnection.D1;
        } else {
            if (position == 0) return VerticalConnection.T0;
            if (position == chainLength - 1) return VerticalConnection.T2;
            return VerticalConnection.T1;
        }
    }

    private boolean isConnectableNeighbor(LevelAccessor level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        return blockState.getBlock() instanceof VerticalConnectBlock
                && (connectOtherVariant || blockState.getBlock() == this);
    }
}
