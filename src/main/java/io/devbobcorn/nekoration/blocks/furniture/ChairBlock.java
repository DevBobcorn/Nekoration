package io.devbobcorn.nekoration.blocks.furniture;

import com.mojang.serialization.MapCodec;

import java.util.Collections;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
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

    private static final double SEAT_THICKNESS = 2.0D;
    private static final double BACK_REST_THICKNESS = 2.0D;

    private final VoxelShape[] chairShapes = new VoxelShape[4];
    private final double seatYOffset;

    public ChairBlock(Properties properties) {
        this(properties, 10, 22);
    }

    public ChairBlock(Properties properties, int seatHeight, int backRestHeight) {
        this(properties, seatHeight, backRestHeight, 2, 2);
    }

    public ChairBlock(Properties properties, int seatHeight, int backRestHeight, int sideInset, int legThickness) {
        super(properties);
        this.seatYOffset = (seatHeight - 8.0D) / 16.0D;
        double seatBottom = seatHeight - SEAT_THICKNESS;
        double legHeight = seatBottom;
        VoxelShape frontLeftLeg = Block.box(sideInset, 0.0D, sideInset, sideInset + legThickness, legHeight,
                sideInset + legThickness);
        VoxelShape frontRightLeg = Block.box(16.0D - sideInset - legThickness, 0.0D, sideInset, 16.0D - sideInset,
                legHeight, sideInset + legThickness);
        VoxelShape backLeftLeg = Block.box(sideInset, 0.0D, 16.0D - sideInset - legThickness, sideInset + legThickness,
                legHeight, 16.0D - sideInset);
        VoxelShape backRightLeg = Block.box(16.0D - sideInset - legThickness, 0.0D, 16.0D - sideInset - legThickness,
                16.0D - sideInset, legHeight, 16.0D - sideInset);
        VoxelShape seat = Block.box(sideInset, seatBottom, sideInset, 16.0D - sideInset, seatHeight,
                16.0D - sideInset);
        VoxelShape baseShape = Shapes.or(frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg, seat);
        VoxelShape[] backRests = new VoxelShape[4];
        backRests[1] = Block.box(16.0D - sideInset - BACK_REST_THICKNESS, seatHeight, sideInset,
                16.0D - sideInset, backRestHeight, 16.0D - sideInset);
        backRests[3] = Block.box(sideInset, seatHeight, sideInset, sideInset + BACK_REST_THICKNESS,
                backRestHeight, 16.0D - sideInset);
        backRests[0] = Block.box(sideInset, seatHeight, sideInset, 16.0D - sideInset, backRestHeight,
                sideInset + BACK_REST_THICKNESS);
        backRests[2] = Block.box(sideInset, seatHeight, 16.0D - sideInset - BACK_REST_THICKNESS, 16.0D - sideInset,
                backRestHeight, 16.0D - sideInset);
        for (int i = 0; i < 4; i++) {
            chairShapes[i] = Shapes.or(backRests[i], baseShape);
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

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(asItem()));
    }
}
