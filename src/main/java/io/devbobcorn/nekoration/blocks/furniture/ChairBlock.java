package io.devbobcorn.nekoration.blocks.furniture;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import io.devbobcorn.nekoration.entities.SeatEntity;

/**
 * Wooden chair with facing-based collision.
 */
public class ChairBlock extends HorizontalDirectionalBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final MapCodec<ChairBlock> CODEC = simpleCodec(ChairBlock::new);

    private static final double SIDE_SPACE = 1.0D;
    private static final double BACK_REST_THICKNESS = 3.0D;

    private final VoxelShape[] chairShapes = new VoxelShape[4];
    private final double seatYOffset;

    public ChairBlock(Properties properties) {
        this(properties, 8, 24);
    }

    public ChairBlock(Properties properties, int seatHeight, int backRestHeight) {
        super(properties);
        this.seatYOffset = (seatHeight - 4.0D) / 16.0D;
        VoxelShape seat = Block.box(SIDE_SPACE, 0.0D, SIDE_SPACE, 16.0D - SIDE_SPACE, seatHeight, 16.0D - SIDE_SPACE);
        VoxelShape[] backRests = new VoxelShape[4];
        backRests[1] = Block.box(16.0D - BACK_REST_THICKNESS, 0.0D, SIDE_SPACE, 16.0D - SIDE_SPACE, backRestHeight,
                16.0D - SIDE_SPACE); // West
        backRests[3] = Block.box(SIDE_SPACE, 0.0D, SIDE_SPACE, BACK_REST_THICKNESS, backRestHeight, 16.0D - SIDE_SPACE); // East
        backRests[0] = Block.box(SIDE_SPACE, 0.0D, SIDE_SPACE, 16.0D - SIDE_SPACE, backRestHeight, BACK_REST_THICKNESS); // South
        backRests[2] = Block.box(SIDE_SPACE, 0.0D, 16.0D - BACK_REST_THICKNESS, 16.0D - SIDE_SPACE, backRestHeight,
                16.0D - SIDE_SPACE); // North
        for (int i = 0; i < 4; i++) {
            chairShapes[i] = Shapes.or(backRests[i], seat);
        }
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return chairShapes[state.getValue(FACING).get2DDataValue()];
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
        return SeatEntity.trySit(level, pos, seatYOffset, player);
    }
}
