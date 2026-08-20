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
        VoxelShape seat = Block.box(1, 8, 1, 15, 10, 14);
        VoxelShape legs = Shapes.or(Block.box(1, 0, 1, 3, 8, 3), Block.box(13, 0, 1, 15, 8, 3),
                Block.box(1, 0, 12, 3, 8, 14), Block.box(13, 0, 12, 15, 8, 14));
        if (facing.getAxis() == Direction.Axis.Z) {
            boolean north = facing == Direction.NORTH;
            double backMin = north ? 12 : 1;
            double backMax = north ? 14 : 3;
            double armMin = north ? 1 : 3;
            double armMax = north ? 13 : 15;
            return Shapes.or(seat, legs, Block.box(1, 10, backMin, 15, 23, backMax),
                    Block.box(0, 7, armMin, 2, 13, armMax), Block.box(14, 7, armMin, 16, 13, armMax));
        }
        boolean east = facing == Direction.EAST;
        double backMin = east ? 1 : 12;
        double backMax = east ? 3 : 14;
        double armMin = east ? 3 : 1;
        double armMax = east ? 15 : 13;
        return Shapes.or(seat, legs, Block.box(backMin, 10, 1, backMax, 23, 14),
                Block.box(armMin, 7, 0, armMax, 13, 2), Block.box(armMin, 7, 14, armMax, 13, 16));
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
        return SeatEntity.trySit(level, pos, 0.25D, player);
    }
}
