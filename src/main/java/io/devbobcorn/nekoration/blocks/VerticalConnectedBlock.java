package io.devbobcorn.nekoration.blocks;

import java.util.ArrayList;
import java.util.List;

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
 * Block with vertical connection.
 */
public class VerticalConnectedBlock extends Block {
    public enum ConnectionType {
        DOUBLE, TRIPLE, PILLAR
    }

    public static final EnumProperty<VerticalConnection> CONNECTION = ModStateProperties.VERTICAL_CONNECTION;

    public final ConnectionType type;
    public final boolean connectOtherVariant;

    public VerticalConnectedBlock(Properties settings) {
        super(settings);
        this.type = ConnectionType.TRIPLE;
        this.connectOtherVariant = false;
    }

    public VerticalConnectedBlock(Properties settings, ConnectionType type, boolean connectOtherVariant) {
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

            boolean connect = stateRef.getBlock() instanceof VerticalConnectedBlock
                    && (connectOtherVariant || stateRef.getBlock() == this);

            if (!connect && config == VerConnectionDir.BOTH) {
                blockPosRef = blockPos.above();
                stateRef = level.getBlockState(blockPosRef);
                connect = stateRef.getBlock() instanceof VerticalConnectedBlock
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
        BlockState res = state;
        VerConnectionDir config = NekoConfig.VER_CONNECTION_DIR.get();
        boolean flag1 = direction == Direction.UP
                && (config == VerConnectionDir.BOTTOM2TOP || config == VerConnectionDir.BOTH);
        boolean flag2 = direction == Direction.DOWN
                && (config == VerConnectionDir.TOP2BOTTOM || config == VerConnectionDir.BOTH);

        boolean connect = flag1 || flag2;
        if (connect && newState.getBlock() instanceof VerticalConnectedBlock
                && (connectOtherVariant || newState.getBlock() == this)) {
            BlockState stateRef;
            if (flag1) {
                stateRef = level.getBlockState(pos.below());
                switch (newState.getValue(CONNECTION)) {
                    case D1 -> {
                        return res.setValue(CONNECTION, VerticalConnection.D0);
                    }
                    case T1 -> {
                        return res.setValue(CONNECTION,
                                (type == ConnectionType.PILLAR && stateRef.getBlock() instanceof VerticalConnectedBlock
                                        && (connectOtherVariant || stateRef.getBlock() == this))
                                        ? VerticalConnection.T1
                                        : VerticalConnection.T0);
                    }
                    case T2 -> {
                        return res.setValue(CONNECTION, VerticalConnection.T1);
                    }
                    default -> {
                    }
                }
            } else {
                stateRef = level.getBlockState(pos.above());
                switch (newState.getValue(CONNECTION)) {
                    case D0 -> {
                        return res.setValue(CONNECTION, VerticalConnection.D1);
                    }
                    case T1 -> {
                        return res.setValue(CONNECTION,
                                (type == ConnectionType.PILLAR && stateRef.getBlock() instanceof VerticalConnectedBlock
                                        && (connectOtherVariant || stateRef.getBlock() == this))
                                        ? VerticalConnection.T1
                                        : VerticalConnection.T2);
                    }
                    case T0 -> {
                        return res.setValue(CONNECTION, VerticalConnection.T1);
                    }
                    default -> {
                    }
                }
            }
        }
        return res;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            VerticalConnection oldConnection = state.getValue(CONNECTION);

            if (connectsUp(oldConnection) && areConnectedPair(state, level.getBlockState(pos.above()))) {
                rebuildConnectionFrom(level, pos.above());
            }
            if (connectsDown(oldConnection) && areConnectedPair(level.getBlockState(pos.below()), state)) {
                rebuildConnectionFrom(level, pos.below());
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected boolean canConnectTo(BlockState state) {
        return state.getBlock() instanceof VerticalConnectedBlock
                && (connectOtherVariant || state.getBlock() == this);
    }

    private static boolean canMutuallyConnect(BlockState a, BlockState b) {
        if (!(a.getBlock() instanceof VerticalConnectedBlock aBlock) || !(b.getBlock() instanceof VerticalConnectedBlock bBlock)) {
            return false;
        }
        return aBlock.canConnectTo(b) && bBlock.canConnectTo(a);
    }

    private static boolean connectsUp(VerticalConnection connection) {
        return connection == VerticalConnection.D0
                || connection == VerticalConnection.T0
                || connection == VerticalConnection.T1;
    }

    private static boolean connectsDown(VerticalConnection connection) {
        return connection == VerticalConnection.D1
                || connection == VerticalConnection.T1
                || connection == VerticalConnection.T2;
    }

    private static boolean areConnectedPair(BlockState lowerState, BlockState upperState) {
        if (!canMutuallyConnect(lowerState, upperState)) {
            return false;
        }
        VerticalConnection lowerConnection = lowerState.getValue(CONNECTION);
        VerticalConnection upperConnection = upperState.getValue(CONNECTION);
        return connectsUp(lowerConnection) && connectsDown(upperConnection);
    }

    private void rebuildConnectionFrom(Level level, BlockPos origin) {
        BlockState originState = level.getBlockState(origin);
        if (!(originState.getBlock() instanceof VerticalConnectedBlock)) {
            return;
        }

        BlockPos start = origin;
        BlockState startState = originState;
        while (true) {
            BlockPos belowPos = start.below();
            BlockState belowState = level.getBlockState(belowPos);
            if (!areConnectedPair(belowState, startState)) {
                break;
            }
            start = belowPos;
            startState = belowState;
        }

        List<BlockPos> segment = new ArrayList<>();
        BlockPos currentPos = start;
        BlockState currentState = level.getBlockState(currentPos);
        while (currentState.getBlock() instanceof VerticalConnectedBlock) {
            segment.add(currentPos);
            BlockPos abovePos = currentPos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (!areConnectedPair(currentState, aboveState)) {
                break;
            }
            currentPos = abovePos;
            currentState = aboveState;
        }

        int size = segment.size();
        if (size <= 0) {
            return;
        }

        for (int i = 0; i < size; i++) {
            BlockPos blockPos = segment.get(i);
            BlockState blockState = level.getBlockState(blockPos);
            if (!(blockState.getBlock() instanceof VerticalConnectedBlock)) {
                continue;
            }

            VerticalConnection connection;
            if (size == 1) {
                connection = VerticalConnection.S0;
            } else if (size == 2) {
                connection = i == 0 ? VerticalConnection.D0 : VerticalConnection.D1;
            } else {
                connection = i == 0 ? VerticalConnection.T0
                        : i == size - 1 ? VerticalConnection.T2 : VerticalConnection.T1;
            }

            if (blockState.getValue(CONNECTION) != connection) {
                level.setBlock(blockPos, blockState.setValue(CONNECTION, connection), Block.UPDATE_CLIENTS);
            }
        }
    }
}
