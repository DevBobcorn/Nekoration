package io.devbobcorn.nekoration.blocks.containers;

import java.util.Map;

import io.devbobcorn.nekoration.blocks.states.HorizontalConnection;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Wooden wall shelf with horizontal connection states.
 */
public class WallShelfBlock extends ItemDisplayBlock {
    public static final EnumProperty<HorizontalConnection> CONNECTION = ModStateProperties.HORIZONTAL_CONNECTION;
    private static final Map<Direction, VoxelShape> SHAPES = getAABBs(6.0D, 16.0D);

    public WallShelfBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(CONNECTION, HorizontalConnection.S0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
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
        boolean connectLeft = canConnectTo(leftState);

        BlockPos rightPos = getRightBlock(pos, facing);
        BlockState rightState = ctx.getLevel().getBlockState(rightPos);
        boolean connectRight = canConnectTo(rightState);

        if (connectLeft && connectRight) {
            return placed.setValue(CONNECTION, HorizontalConnection.T1);
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
        if (connect && canConnectTo(neighborState)) {
            BlockState stateRef;
            if (flag1) {
                stateRef = level.getBlockState(getLeftBlock(pos, facing));
                return switch (neighborState.getValue(CONNECTION)) {
                    case D1 -> res.setValue(CONNECTION, HorizontalConnection.D0);
                    case T1 -> res.setValue(CONNECTION, canConnectTo(stateRef) ? HorizontalConnection.T1 : HorizontalConnection.T0);
                    case T2 -> res.setValue(CONNECTION, HorizontalConnection.T1);
                    default -> res;
                };
            }
            stateRef = level.getBlockState(getRightBlock(pos, facing));
            return switch (neighborState.getValue(CONNECTION)) {
                case D0 -> res.setValue(CONNECTION, HorizontalConnection.D1);
                case T1 -> res.setValue(CONNECTION, canConnectTo(stateRef) ? HorizontalConnection.T1 : HorizontalConnection.T2);
                case T0 -> res.setValue(CONNECTION, HorizontalConnection.T1);
                default -> res;
            };
        }
        return res;
    }

    private boolean canConnectTo(BlockState state) {
        return state.getBlock() instanceof WallShelfBlock && state.getBlock() == this;
    }

    private HorizontalConnection nextFromLeft(HorizontalConnection leftConnection) {
        return switch (leftConnection) {
            case S0, D0, T0 -> HorizontalConnection.D1;
            case D1, T1, T2 -> HorizontalConnection.T2;
        };
    }

    private HorizontalConnection nextFromRight(HorizontalConnection rightConnection) {
        return switch (rightConnection) {
            case S0, D1, T2 -> HorizontalConnection.D0;
            case D0, T1, T0 -> HorizontalConnection.T0;
        };
    }

    private static BlockPos getLeftBlock(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.east();
            case EAST -> pos.south();
            case SOUTH -> pos.west();
            default -> pos.north();
        };
    }

    private static BlockPos getRightBlock(BlockPos pos, Direction dir) {
        return switch (dir) {
            case NORTH -> pos.west();
            case EAST -> pos.north();
            case SOUTH -> pos.east();
            default -> pos.south();
        };
    }

    private static Direction getLeftDir(Direction selfDir) {
        return switch (selfDir) {
            case NORTH -> Direction.EAST;
            case EAST -> Direction.SOUTH;
            case SOUTH -> Direction.WEST;
            default -> Direction.NORTH;
        };
    }

    private static Direction getRightDir(Direction selfDir) {
        return switch (selfDir) {
            case NORTH -> Direction.WEST;
            case EAST -> Direction.NORTH;
            case SOUTH -> Direction.EAST;
            default -> Direction.SOUTH;
        };
    }
}
