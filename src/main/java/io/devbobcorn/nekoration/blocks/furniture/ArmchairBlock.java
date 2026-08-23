package io.devbobcorn.nekoration.blocks.furniture;

import com.mojang.serialization.MapCodec;

import io.devbobcorn.nekoration.entities.SeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Wooden armchair which can be used as a seat. */
public class ArmchairBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<ArmchairBlock> CODEC = simpleCodec(ArmchairBlock::new);

    private final VoxelShape[] shapes = new VoxelShape[4];

    public ArmchairBlock(Properties properties) {
        super(properties);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            shapes[facing.get2DDataValue()] = createShape(facing);
        }
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    private static VoxelShape createShape(Direction facing) {
        VoxelShape seat;
        VoxelShape back;
        VoxelShape legs;
        VoxelShape arms;
        if (facing.getAxis() == Direction.Axis.Z) {
            boolean north = facing == Direction.NORTH;
            double seatMinZ = north ? 1 : 3;
            double seatMaxZ = north ? 13 : 15;
            double backMinZ = north ? 13 : 1;
            double backMaxZ = north ? 15 : 3;
            double frontMinZ = north ? 2 : 12;
            double frontMaxZ = north ? 4 : 14;
            double rearMinZ = north ? 13 : 1;
            double rearMaxZ = north ? 15 : 3;
            seat = Block.box(2, 5, seatMinZ, 14, 7, seatMaxZ);
            back = Block.box(1, 5, backMinZ, 15, 19, backMaxZ);
            legs = Shapes.or(Block.box(0, 0, frontMinZ, 2, 9, frontMaxZ),
                    Block.box(14, 0, frontMinZ, 16, 9, frontMaxZ), Block.box(1, 0, rearMinZ, 3, 5, rearMaxZ),
                    Block.box(13, 0, rearMinZ, 15, 5, rearMaxZ));
            arms = Shapes.or(Block.box(0, 9, 2, 2, 11, 14), Block.box(14, 9, 2, 16, 11, 14));
        } else {
            boolean east = facing == Direction.EAST;
            double seatMinX = east ? 3 : 1;
            double seatMaxX = east ? 15 : 13;
            double backMinX = east ? 1 : 13;
            double backMaxX = east ? 3 : 15;
            double frontMinX = east ? 12 : 2;
            double frontMaxX = east ? 14 : 4;
            double rearMinX = east ? 1 : 13;
            double rearMaxX = east ? 3 : 15;
            seat = Block.box(seatMinX, 5, 2, seatMaxX, 7, 14);
            back = Block.box(backMinX, 5, 1, backMaxX, 19, 15);
            legs = Shapes.or(Block.box(frontMinX, 0, 0, frontMaxX, 9, 2),
                    Block.box(frontMinX, 0, 14, frontMaxX, 9, 16), Block.box(rearMinX, 0, 1, rearMaxX, 5, 3),
                    Block.box(rearMinX, 0, 13, rearMaxX, 5, 15));
            arms = Shapes.or(Block.box(2, 9, 0, 14, 11, 2), Block.box(2, 9, 14, 14, 11, 16));
        }
        return Shapes.or(seat, back, legs, arms);
    }

    @Override
    protected MapCodec<ArmchairBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
            BlockHitResult hitResult) {
        return SeatEntity.trySit(level, pos, 0D, player);
    }
}
