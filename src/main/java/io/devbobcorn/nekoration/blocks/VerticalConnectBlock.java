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
        BlockState res = state;
        VerConnectionDir config = NekoConfig.VER_CONNECTION_DIR.get();
        boolean flag1 = direction == Direction.UP
                && (config == VerConnectionDir.BOTTOM2TOP || config == VerConnectionDir.BOTH);
        boolean flag2 = direction == Direction.DOWN
                && (config == VerConnectionDir.TOP2BOTTOM || config == VerConnectionDir.BOTH);

        boolean connect = flag1 || flag2;
        if (connect && newState.getBlock() instanceof VerticalConnectBlock
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
                                (type == ConnectionType.PILLAR && stateRef.getBlock() instanceof VerticalConnectBlock
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
                                (type == ConnectionType.PILLAR && stateRef.getBlock() instanceof VerticalConnectBlock
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
}
