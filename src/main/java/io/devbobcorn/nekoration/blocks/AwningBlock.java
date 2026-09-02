package io.devbobcorn.nekoration.blocks;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Dyeable awning with an axe-toggleable end cap.
 *
 * <p>An awning placed right below the front of another awning faces the same way, so the two
 * visually connect into a longer descending awning; the awning being continued then stops being
 * an end piece, and becomes one again once the awning continuing it is removed.
 */
public class AwningBlock extends DyeableHorizontalBlock {
    public static final BooleanProperty IS_END = BlockStateProperties.BOTTOM;
    public static final MapCodec<AwningBlock> CODEC = simpleCodec(AwningBlock::new);

    /** Height of the bottom step of the awning. */
    private static final double BOTTOM_STEP_HEIGHT = 4.0D / 16.0D;

    /** Tolerance when checking which part of an awning is being looked at. */
    private static final double HIT_EPSILON = 1.0D / 128.0D;

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 4.0D),
            Block.box(0.0D, 4.0D, 4.0D, 16.0D, 8.0D, 8.0D),
            Block.box(0.0D, 8.0D, 8.0D, 16.0D, 12.0D, 12.0D),
            Block.box(0.0D, 12.0D, 12.0D, 16.0D, 16.0D, 16.0D));
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(0.0D, 0.0D, 12.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 4.0D, 8.0D, 16.0D, 8.0D, 12.0D),
            Block.box(0.0D, 8.0D, 4.0D, 16.0D, 12.0D, 8.0D),
            Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 4.0D));
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(12.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(8.0D, 4.0D, 0.0D, 12.0D, 8.0D, 16.0D),
            Block.box(4.0D, 8.0D, 0.0D, 8.0D, 12.0D, 16.0D),
            Block.box(0.0D, 12.0D, 0.0D, 4.0D, 16.0D, 16.0D));
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 4.0D, 4.0D, 16.0D),
            Block.box(4.0D, 4.0D, 0.0D, 8.0D, 8.0D, 16.0D),
            Block.box(8.0D, 8.0D, 0.0D, 12.0D, 12.0D, 16.0D),
            Block.box(12.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D));

    public AwningBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(IS_END, true));
    }

    @Override
    protected MapCodec<AwningBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_END);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState placed = super.getStateForPlacement(ctx);
        if (placed == null) {
            return null;
        }

        // Continue an awning whose front this placement would sit right below.
        BlockPos pos = ctx.getClickedPos();
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState upper = ctx.getLevel().getBlockState(pos.relative(direction).above());
            if (upper.getBlock() instanceof AwningBlock
                    && upper.getValue(FACING) == direction.getOpposite()) {
                return placed.setValue(FACING, direction.getOpposite());
            }
        }
        return placed;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        // The awning being continued is no longer an end piece.
        Direction facing = state.getValue(FACING);
        BlockPos upperPos = pos.relative(facing.getOpposite()).above();
        BlockState upper = level.getBlockState(upperPos);
        if (upper.getBlock() instanceof AwningBlock
                && upper.getValue(FACING) == facing
                && upper.getValue(IS_END)) {
            level.setBlock(upperPos, upper.setValue(IS_END, false), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()) {
            // The awning above is an end piece again, now that nothing continues it.
            Direction facing = state.getValue(FACING);
            BlockPos upperPos = pos.relative(facing.getOpposite()).above();
            BlockState upper = level.getBlockState(upperPos);
            if (upper.getBlock() instanceof AwningBlock
                    && upper.getValue(FACING) == facing
                    && !upper.getValue(IS_END)) {
                level.setBlock(upperPos, upper.setValue(IS_END, true), Block.UPDATE_ALL);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    /**
     * Returns the position right below the position in front of the awning at {@code targetPos}
     * (where another awning would visually connect to it) when {@code hitLocation} lies on the
     * front side of its bottom step, or {@code null} otherwise.
     */
    @Nullable
    public static BlockPos getConnectedPlacementPos(BlockGetter level, BlockPos targetPos, Direction hitFace,
            Vec3 hitLocation) {
        BlockState target = level.getBlockState(targetPos);
        if (!(target.getBlock() instanceof AwningBlock)) {
            return null;
        }
        Direction facing = target.getValue(FACING);
        if (hitFace != facing) {
            return null;
        }

        // Only the front of the bottom step, flush with the cell boundary, triggers the connected
        // placement; the risers of the higher steps lie further inside the block.
        double x = hitLocation.x - targetPos.getX();
        double y = hitLocation.y - targetPos.getY();
        double z = hitLocation.z - targetPos.getZ();
        double depth = facing.getAxis().choose(x, y, z);
        double frontPlane = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D : 0.0D;
        if (Math.abs(depth - frontPlane) > HIT_EPSILON
                || y < -HIT_EPSILON
                || y > BOTTOM_STEP_HEIGHT + HIT_EPSILON) {
            return null;
        }
        return targetPos.relative(facing).below();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof AxeItem) {
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }
            level.setBlock(pos, state.cycle(IS_END), Block.UPDATE_ALL);
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_NORTH;
        }
    }
}
