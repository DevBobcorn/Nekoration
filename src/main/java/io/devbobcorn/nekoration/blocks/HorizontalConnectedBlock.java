package io.devbobcorn.nekoration.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Block with horizontal connection.
 */
public class HorizontalConnectedBlock extends HorizontalBlock {
    public enum ConnectionType {
        DOUBLE, TRIPLE, BEAM
    }

    public static final EnumProperty<HorizontalConnection> CONNECTION = ModStateProperties.HORIZONTAL_CONNECTION;

    private final Map<Direction, VoxelShape> shapes;
    public final ConnectionType type;
    public final boolean connectOtherVariant;

    public HorizontalConnectedBlock(Properties settings, int thickness, int height, int bottom) {
        this(settings, ConnectionType.TRIPLE, false, thickness, height, bottom);
    }

    public HorizontalConnectedBlock(Properties settings, ConnectionType type, boolean connectOtherVariant, int thickness, int height, int bottom) {
        super(settings);
        this.type = type;
        this.connectOtherVariant = connectOtherVariant;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, HorizontalConnection.S0));
        this.shapes = getAABBs(thickness, height, bottom);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }

        if (ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown()) {
            return placed.setValue(CONNECTION, HorizontalConnection.S0);
        }

        Direction facing = placed.getValue(FACING);
        BlockPos pos = ctx.getClickedPos();

        BlockPos leftPos = getLeftBlock(pos, facing);
        BlockState leftState = ctx.getLevel().getBlockState(leftPos);
        boolean connectLeft = canConnectTo(leftState, facing);

        BlockPos rightPos = getRightBlock(pos, facing);
        BlockState rightState = ctx.getLevel().getBlockState(rightPos);
        boolean connectRight = canConnectTo(rightState, facing);

        if (connectLeft && connectRight) {
            Direction leftDir = getLeftDir(facing);
            Direction rightDir = getRightDir(facing);
            Direction clickedFace = ctx.getClickedFace();

            if (clickedFace == rightDir) {
                connectRight = false;
            } else if (clickedFace == leftDir) {
                connectLeft = false;
            } else if (type == ConnectionType.BEAM) {
                return placed.setValue(CONNECTION, HorizontalConnection.T1);
            } else {
                connectLeft = false;
                connectRight = false;
            }
        }

        if (connectLeft) {
            HorizontalConnection connection = nextFromLeft(leftState.getValue(CONNECTION));
            return placed.setValue(CONNECTION, connection);
        }

        if (connectRight) {
            HorizontalConnection connection = nextFromRight(rightState.getValue(CONNECTION));
            return placed.setValue(CONNECTION, connection);
        }

        return placed.setValue(CONNECTION, HorizontalConnection.S0);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        BlockState res = state;
        Direction facing = state.getValue(FACING);

        boolean flag1 = direction == getRightDir(facing);
        boolean flag2 = direction == getLeftDir(facing);

        boolean connect = flag1 || flag2;
        if (connect && canConnectTo(neighborState, facing)) {
            BlockState stateRef;
            if (flag1) {
                stateRef = level.getBlockState(getLeftBlock(pos, facing));
                return switch (neighborState.getValue(CONNECTION)) {
                    case D1 -> res.setValue(CONNECTION, HorizontalConnection.D0);
                    case T1 -> res.setValue(CONNECTION,
                            (type == ConnectionType.BEAM && canConnectTo(stateRef, facing)
                                    && connectsRight(stateRef.getValue(CONNECTION)))
                                    ? HorizontalConnection.T1
                                    : HorizontalConnection.T0);
                    case T2 -> res.setValue(CONNECTION, HorizontalConnection.T1);
                    default -> res;
                };
            } else {
                stateRef = level.getBlockState(getRightBlock(pos, facing));
                return switch (neighborState.getValue(CONNECTION)) {
                    case D0 -> res.setValue(CONNECTION, HorizontalConnection.D1);
                    case T1 -> res.setValue(CONNECTION,
                            (type == ConnectionType.BEAM && canConnectTo(stateRef, facing)
                                    && connectsLeft(stateRef.getValue(CONNECTION)))
                                    ? HorizontalConnection.T1
                                    : HorizontalConnection.T2);
                    case T0 -> res.setValue(CONNECTION, HorizontalConnection.T1);
                    default -> res;
                };
            }
        }
        return res;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            Direction facing = state.getValue(FACING);
            HorizontalConnection oldConnection = state.getValue(CONNECTION);

            BlockPos rightPos = getRightBlock(pos, facing);
            BlockPos leftPos = getLeftBlock(pos, facing);
            BlockState rightState = level.getBlockState(rightPos);
            BlockState leftState = level.getBlockState(leftPos);

            if (connectsRight(oldConnection) && areConnectedPair(state, rightState)) {
                rebuildConnectionFrom(level, rightPos);
            }
            if (connectsLeft(oldConnection) && areConnectedPair(leftState, state)) {
                rebuildConnectionFrom(level, leftPos);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected boolean canConnectTo(BlockState state, Direction facing) {
        return state.getBlock() instanceof HorizontalConnectedBlock
                && (connectOtherVariant || state.getBlock() == this)
                && state.getValue(FACING) == facing;
    }

    private static boolean canMutuallyConnect(BlockState a, BlockState b) {
        if (!(a.getBlock() instanceof HorizontalConnectedBlock aBlock) || !(b.getBlock() instanceof HorizontalConnectedBlock bBlock)) {
            return false;
        }
        return aBlock.canConnectTo(b, a.getValue(FACING)) && bBlock.canConnectTo(a, b.getValue(FACING));
    }

    private static boolean connectsRight(HorizontalConnection connection) {
        return connection == HorizontalConnection.D0
                || connection == HorizontalConnection.T0
                || connection == HorizontalConnection.T1;
    }

    private static boolean connectsLeft(HorizontalConnection connection) {
        return connection == HorizontalConnection.D1
                || connection == HorizontalConnection.T1
                || connection == HorizontalConnection.T2;
    }

    private static boolean areConnectedPair(BlockState leftState, BlockState rightState) {
        if (!canMutuallyConnect(leftState, rightState)) {
            return false;
        }
        if (leftState.getValue(FACING) != rightState.getValue(FACING)) {
            return false;
        }
        HorizontalConnection leftConnection = leftState.getValue(CONNECTION);
        HorizontalConnection rightConnection = rightState.getValue(CONNECTION);
        return connectsRight(leftConnection) && connectsLeft(rightConnection);
    }

    private void rebuildConnectionFrom(Level level, BlockPos origin) {
        BlockState originState = level.getBlockState(origin);
        if (!(originState.getBlock() instanceof HorizontalConnectedBlock)) {
            return;
        }

        Direction facing = originState.getValue(FACING);
        BlockPos start = origin;
        while (true) {
            BlockPos leftPos = getLeftBlock(start, facing);
            BlockState leftState = level.getBlockState(leftPos);
            BlockState startState = level.getBlockState(start);
            if (!areConnectedPair(leftState, startState)) {
                break;
            }
            start = leftPos;
        }

        List<BlockPos> segment = new ArrayList<>();
        BlockPos currentPos = start;
        while (true) {
            BlockState currentState = level.getBlockState(currentPos);
            if (!(currentState.getBlock() instanceof HorizontalConnectedBlock)
                    || currentState.getValue(FACING) != facing) {
                break;
            }

            segment.add(currentPos);
            BlockPos rightPos = getRightBlock(currentPos, facing);
            BlockState rightState = level.getBlockState(rightPos);
            if (!areConnectedPair(currentState, rightState)) {
                break;
            }
            currentPos = rightPos;
        }

        int size = segment.size();
        if (size <= 0) {
            return;
        }

        for (int i = 0; i < size; i++) {
            BlockPos blockPos = segment.get(i);
            BlockState blockState = level.getBlockState(blockPos);
            if (!(blockState.getBlock() instanceof HorizontalConnectedBlock)
                    || blockState.getValue(FACING) != facing) {
                continue;
            }

            HorizontalConnection connection;
            if (size == 1) {
                connection = HorizontalConnection.S0;
            } else if (size == 2) {
                connection = i == 0 ? HorizontalConnection.D0 : HorizontalConnection.D1;
            } else {
                connection = i == 0 ? HorizontalConnection.T0
                        : i == size - 1 ? HorizontalConnection.T2 : HorizontalConnection.T1;
            }

            if (blockState.getValue(CONNECTION) != connection) {
                level.setBlock(blockPos, blockState.setValue(CONNECTION, connection), Block.UPDATE_CLIENTS);
            }
        }
    }

    private HorizontalConnection nextFromLeft(HorizontalConnection leftConnection) {
        return switch (leftConnection) {
            case S0, D0, T0 -> HorizontalConnection.D1;
            case D1 -> type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T2;
            case T1 -> HorizontalConnection.T2;
            case T2 -> type == ConnectionType.BEAM ? HorizontalConnection.T2 : HorizontalConnection.S0;
        };
    }

    private HorizontalConnection nextFromRight(HorizontalConnection rightConnection) {
        return switch (rightConnection) {
            case S0, D1, T2 -> HorizontalConnection.D0;
            case D0 -> type == ConnectionType.DOUBLE ? HorizontalConnection.S0 : HorizontalConnection.T0;
            case T1 -> HorizontalConnection.T0;
            case T0 -> type == ConnectionType.BEAM ? HorizontalConnection.T0 : HorizontalConnection.S0;
        };
    }
}
