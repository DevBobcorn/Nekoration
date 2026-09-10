package io.devbobcorn.nekoration.blocks.stone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.devbobcorn.nekoration.blocks.HorizontalBlock;
import io.devbobcorn.nekoration.blocks.states.FrameConnection;
import io.devbobcorn.nekoration.blocks.states.ModStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
        BlockState existing = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (isSameKind(existing, ctx.getItemInHand()) && existing.getValue(CONNECTION) != FrameConnection.BOTH) {
            return existing.setValue(CONNECTION, FrameConnection.BOTH);
        }

        BlockPos pos = ctx.getClickedPos();
        Vec3 click = ctx.getClickLocation();
        double x = click.x - pos.getX();
        double z = click.z - pos.getZ();

        Direction facing = getFacingAt(x, z);
        FrameConnection connection = getConnectionAt(facing, x, z);
        return this.defaultBlockState().setValue(FACING, facing).setValue(CONNECTION, connection);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return !useContext.replacingClickedOnBlock()
                && isSameKind(state, useContext.getItemInHand())
                && state.getValue(CONNECTION) != FrameConnection.BOTH;
    }

    /** Number of items this frame side drops: one per side (left/right) of the frame still present. */
    protected int getDropCount(BlockState state) {
        return state.getValue(CONNECTION) == FrameConnection.BOTH ? 2 : 1;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = new ArrayList<>(getDropCount(state));
        for (int i = 0; i < getDropCount(state); i++) {
            drops.add(new ItemStack(asItem()));
        }
        return drops;
    }

    protected boolean isSameKind(BlockState state, ItemStack stack) {
        return state.getBlock() == this
                && stack.getItem() instanceof BlockItem item
                && item.getBlock() == this;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest,
            FluidState fluid) {
        if (player != null && state.getValue(CONNECTION) == FrameConnection.BOTH) {
            FrameConnection remaining = getRemainingConnection(state, level, pos, player);
            if (remaining != null) {
                level.setBlock(pos, state.setValue(CONNECTION, remaining), Block.UPDATE_ALL);
                if (willHarvest) {
                    player.awardStat(Stats.BLOCK_MINED.get(this));
                    player.causeFoodExhaustion(0.005F);
                    Block.popResource(level, pos, this.getCloneItemStack(level, pos, state));
                }
                return false;
            }
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    private static FrameConnection getRemainingConnection(BlockState state, Level level, BlockPos pos, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0F).scale(player.blockInteractionRange() + 1.0));
        BlockHitResult hit = state.getShape(level, pos).clip(start, end, pos);
        if (hit == null) {
            return null;
        }

        Vec3 location = hit.getLocation();
        double x = location.x - pos.getX();
        double z = location.z - pos.getZ();
        FrameConnection broken = getConnectionAt(state.getValue(FACING), x, z);
        return broken == FrameConnection.LEFT ? FrameConnection.RIGHT : FrameConnection.LEFT;
    }

    private static Direction getFacingAt(double x, double z) {
        Direction facing = Direction.NORTH;
        double distance = 1.0 - z;
        if (z < distance) {
            facing = Direction.SOUTH;
            distance = z;
        }
        if (x < distance) {
            facing = Direction.EAST;
            distance = x;
        }
        if (1.0 - x < distance) {
            facing = Direction.WEST;
        }
        return facing;
    }

    private static FrameConnection getConnectionAt(Direction facing, double x, double z) {
        Direction left = getLeftDir(facing);
        double coord = left.getAxis() == Direction.Axis.X ? x : z;
        boolean isLeft = left.getAxisDirection() == Direction.AxisDirection.POSITIVE ? coord >= 0.5 : coord < 0.5;
        return isLeft ? FrameConnection.LEFT : FrameConnection.RIGHT;
    }
}
