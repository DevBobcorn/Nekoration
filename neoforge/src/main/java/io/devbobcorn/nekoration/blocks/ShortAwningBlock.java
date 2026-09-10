package io.devbobcorn.nekoration.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Dyeable short awning. */
public class ShortAwningBlock extends DyeableHorizontalBlock {
    public static final MapCodec<ShortAwningBlock> CODEC = simpleCodec(ShortAwningBlock::new);

    private static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0.0D, 8.0D, 0.0D, 16.0D, 12.0D, 5.333333D),
            Block.box(0.0D, 10.0D, 5.333333D, 16.0D, 14.0D, 10.666667D),
            Block.box(0.0D, 12.0D, 10.666667D, 16.0D, 16.0D, 16.0D));
    private static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(0.0D, 8.0D, 10.666667D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 10.0D, 5.333333D, 16.0D, 14.0D, 10.666667D),
            Block.box(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 5.333333D));
    private static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(10.666667D, 8.0D, 0.0D, 16.0D, 12.0D, 16.0D),
            Block.box(5.333333D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D),
            Block.box(0.0D, 12.0D, 0.0D, 5.333333D, 16.0D, 16.0D));
    private static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(0.0D, 8.0D, 0.0D, 5.333333D, 12.0D, 16.0D),
            Block.box(5.333333D, 10.0D, 0.0D, 10.666667D, 14.0D, 16.0D),
            Block.box(10.666667D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D));

    public ShortAwningBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<ShortAwningBlock> codec() {
        return CODEC;
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
