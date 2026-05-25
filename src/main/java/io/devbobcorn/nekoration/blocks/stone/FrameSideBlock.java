package io.devbobcorn.nekoration.blocks.stone;

import java.util.Map;

import io.devbobcorn.nekoration.blocks.HorizontalBlock;
import io.devbobcorn.nekoration.blocks.HorizontalConnectedBlock;
import io.devbobcorn.nekoration.blocks.states.FrameConnection;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Block with connection to other frame parts.
 */
public class FrameSideBlock extends HorizontalBlock {
    public static final EnumProperty<FrameConnection> CONNECTION = ModStateProperties.FRAME_CONNECTION;

    private final Map<Direction, Map<FrameConnection, VoxelShape>> shapes;

    public FrameSideBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(CONNECTION, FrameConnection.BOTH));
        this.shapes = createShapes();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTION);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes.get(state.getValue(FACING)).get(state.getValue(CONNECTION));
    }

    private static Map<Direction, Map<FrameConnection, VoxelShape>> createShapes() {
        VoxelShape leftNorth = box(13.0D, 0.0D, 14.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape rightNorth = box(0.0D, 0.0D, 14.0D, 3.0D, 16.0D, 16.0D);
        VoxelShape bothNorth = Shapes.or(leftNorth, rightNorth);

        VoxelShape leftSouth = box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 2.0D);
        VoxelShape rightSouth = box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 2.0D);
        VoxelShape bothSouth = Shapes.or(leftSouth, rightSouth);

        VoxelShape leftEast = box(0.0D, 0.0D, 13.0D, 2.0D, 16.0D, 16.0D);
        VoxelShape rightEast = box(0.0D, 0.0D, 0.0D, 2.0D, 16.0D, 3.0D);
        VoxelShape bothEast = Shapes.or(leftEast, rightEast);

        VoxelShape leftWest = box(14.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
        VoxelShape rightWest = box(14.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
        VoxelShape bothWest = Shapes.or(leftWest, rightWest);

        return Map.of(
                Direction.NORTH, Map.of(
                        FrameConnection.LEFT, leftNorth,
                        FrameConnection.RIGHT, rightNorth,
                        FrameConnection.BOTH, bothNorth),
                Direction.SOUTH, Map.of(
                        FrameConnection.LEFT, leftSouth,
                        FrameConnection.RIGHT, rightSouth,
                        FrameConnection.BOTH, bothSouth),
                Direction.EAST, Map.of(
                        FrameConnection.LEFT, leftEast,
                        FrameConnection.RIGHT, rightEast,
                        FrameConnection.BOTH, bothEast),
                Direction.WEST, Map.of(
                        FrameConnection.LEFT, leftWest,
                        FrameConnection.RIGHT, rightWest,
                        FrameConnection.BOTH, bothWest));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }
        Direction facing = placed.getValue(FACING);
        FrameConnection connection = getConnection(ctx.getLevel(), ctx.getClickedPos(), facing);
        return placed.setValue(CONNECTION, connection);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level,
            BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        FrameConnection connection = getConnection(level, pos, facing);
        return state.setValue(CONNECTION, connection);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            Direction facing = state.getValue(FACING);

            BlockPos right = getRightBlock(pos, facing);
            BlockPos left = getLeftBlock(pos, facing);

            recalculateNeighbor(level, right);
            recalculateNeighbor(level, right.above());
            recalculateNeighbor(level, right.below());
            recalculateNeighbor(level, left);
            recalculateNeighbor(level, left.above());
            recalculateNeighbor(level, left.below());
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    protected boolean canConnectTo(BlockState state, Direction facing) {
        return state.getBlock() instanceof HorizontalConnectedBlock
                && state.getValue(FACING) == facing;
    }

    private FrameConnection getConnection(LevelAccessor level, BlockPos pos, Direction facing) {
        BlockPos left = getLeftBlock(pos, facing);
        BlockPos right = getRightBlock(pos, facing);

        boolean connectLeft = canConnectTo(level.getBlockState(left.above()), facing)
                || canConnectTo(level.getBlockState(left), facing)
                || canConnectTo(level.getBlockState(left.below()), facing);
        boolean connectRight = canConnectTo(level.getBlockState(right.above()), facing)
                || canConnectTo(level.getBlockState(right), facing)
                || canConnectTo(level.getBlockState(right.below()), facing);

        BlockState aboveState = level.getBlockState(pos.above());
        BlockState belowState = level.getBlockState(pos.below());
        connectLeft = connectLeft || hasLeftConnection(aboveState, facing) || hasLeftConnection(belowState, facing);
        connectRight = connectRight || hasRightConnection(aboveState, facing) || hasRightConnection(belowState, facing);

        if (connectLeft && !connectRight) {
            return FrameConnection.LEFT;
        }
        if (connectRight && !connectLeft) {
            return FrameConnection.RIGHT;
        }
        return FrameConnection.BOTH;
    }

    private boolean hasLeftConnection(BlockState state, Direction facing) {
        if (!(state.getBlock() instanceof FrameSideBlock) || state.getValue(FACING) != facing) {
            return false;
        }
        FrameConnection connection = state.getValue(CONNECTION);
        return connection == FrameConnection.LEFT || connection == FrameConnection.BOTH;
    }

    private boolean hasRightConnection(BlockState state, Direction facing) {
        if (!(state.getBlock() instanceof FrameSideBlock) || state.getValue(FACING) != facing) {
            return false;
        }
        FrameConnection connection = state.getValue(CONNECTION);
        return connection == FrameConnection.RIGHT || connection == FrameConnection.BOTH;
    }

    private void recalculateNeighbor(Level level, BlockPos neighborPos) {
        BlockState neighborState = level.getBlockState(neighborPos);
        if (!(neighborState.getBlock() instanceof FrameSideBlock)) {
            return;
        }

        Direction neighborFacing = neighborState.getValue(FACING);
        FrameConnection newConnection = getConnection(level, neighborPos, neighborFacing);
        if (neighborState.getValue(CONNECTION) != newConnection) {
            level.setBlock(neighborPos, neighborState.setValue(CONNECTION, newConnection), Block.UPDATE_CLIENTS);
        }
    }
}
